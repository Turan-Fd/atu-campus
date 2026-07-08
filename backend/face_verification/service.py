import base64
import math
import os
from typing import Any

import cv2
import numpy as np
import onnxruntime as ort
import requests
from fastapi import FastAPI
from pydantic import BaseModel, Field


app = FastAPI(title="ATU Face Verification Service", version="v1")

FACE_SIMILARITY_THRESHOLD = float(os.getenv("FACE_SIMILARITY_THRESHOLD", "0.75"))
LIVENESS_SCORE_THRESHOLD = float(os.getenv("LIVENESS_SCORE_THRESHOLD", "0.90"))
FACE_SIMILARITY_SOFT_FLOOR = float(os.getenv("FACE_SIMILARITY_SOFT_FLOOR", "0.72"))
LIVENESS_SCORE_SOFT_FLOOR = float(os.getenv("LIVENESS_SCORE_SOFT_FLOOR", "0.86"))
MIN_FOCUS_SCORE = float(os.getenv("FACE_MIN_FOCUS_SCORE", "45.0"))
MIN_BRIGHTNESS = float(os.getenv("FACE_MIN_BRIGHTNESS", "35.0"))
MAX_BRIGHTNESS = float(os.getenv("FACE_MAX_BRIGHTNESS", "220.0"))
SFACE_DETECTOR_MODEL_PATH = os.getenv("SFACE_DETECTOR_MODEL_PATH", "").strip()
SFACE_RECOGNIZER_MODEL_PATH = os.getenv("SFACE_RECOGNIZER_MODEL_PATH", "").strip()
MINIFAS_MODEL_PATH = os.getenv(
    "MINIFAS_MODEL_PATH",
    os.path.join(os.path.dirname(__file__), "models", "best_model_quantized.onnx"),
).strip()


class VerifyRequest(BaseModel):
    referenceImageUrl: str | None = None
    referenceImageBase64: str | None = None
    referenceImageMimeType: str | None = None
    captureImages: list[str] = Field(default_factory=list)
    challengeMeta: dict[str, Any] = Field(default_factory=dict)


class FaceEngine:
    def __init__(self) -> None:
        self.haar = cv2.CascadeClassifier(
            cv2.data.haarcascades + "haarcascade_frontalface_default.xml"
        )
        self.detector = None
        self.recognizer = None
        self.mode = "fallback"

        can_use_sface = (
            hasattr(cv2, "FaceDetectorYN")
            and hasattr(cv2, "FaceRecognizerSF")
            and os.path.exists(SFACE_DETECTOR_MODEL_PATH)
            and os.path.exists(SFACE_RECOGNIZER_MODEL_PATH)
        )
        if can_use_sface:
            try:
                self.detector = cv2.FaceDetectorYN.create(
                    SFACE_DETECTOR_MODEL_PATH,
                    "",
                    (320, 320),
                )
                self.recognizer = cv2.FaceRecognizerSF.create(
                    SFACE_RECOGNIZER_MODEL_PATH,
                    "",
                )
                self.mode = "sface"
            except Exception:
                self.detector = None
                self.recognizer = None
                self.mode = "fallback"

    def extract_face(self, image: np.ndarray) -> tuple[np.ndarray | None, dict[str, Any]]:
        if image is None or image.size == 0:
            return None, {"detector": self.mode, "reason": "empty_image"}

        if self.mode == "sface" and self.detector is not None and self.recognizer is not None:
            return self._extract_with_sface(image)

        return self._extract_with_haar(image)

    def _extract_with_haar(self, image: np.ndarray) -> tuple[np.ndarray | None, dict[str, Any]]:
        gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
        faces = self.haar.detectMultiScale(
            gray,
            scaleFactor=1.1,
            minNeighbors=5,
            minSize=(80, 80),
        )
        if len(faces) == 0:
            return None, {"detector": "haar", "reason": "no_face_detected"}

        x, y, w, h = max(faces, key=lambda item: item[2] * item[3])
        face = image[y : y + h, x : x + w]
        return face, {
            "detector": "haar",
            "box": [int(x), int(y), int(w), int(h)],
        }

    def _extract_with_sface(self, image: np.ndarray) -> tuple[np.ndarray | None, dict[str, Any]]:
        height, width = image.shape[:2]
        self.detector.setInputSize((width, height))
        _, faces = self.detector.detect(image)
        if faces is None or len(faces) == 0:
            return None, {"detector": "sface", "reason": "no_face_detected"}

        face = faces[0]
        aligned = self.recognizer.alignCrop(image, face)
        x, y, w, h = [int(value) for value in face[:4]]
        return aligned, {
            "detector": "sface",
            "box": [x, y, w, h],
        }

    def similarity(self, face_a: np.ndarray, face_b: np.ndarray) -> float:
        if self.mode == "sface" and self.recognizer is not None:
            try:
                feat_a = self.recognizer.feature(face_a)
                feat_b = self.recognizer.feature(face_b)
                score = self.recognizer.match(feat_a, feat_b, cv2.FaceRecognizerSF_FR_COSINE)
                return float(max(0.0, min(1.0, score)))
            except Exception:
                pass
        return fallback_similarity(face_a, face_b)


engine = FaceEngine()


class AntiSpoofEngine:
    def __init__(self) -> None:
        self.session: ort.InferenceSession | None = None
        self.input_name = ""
        self.mode = "fallback"
        if os.path.exists(MINIFAS_MODEL_PATH):
            try:
                self.session = ort.InferenceSession(
                    MINIFAS_MODEL_PATH,
                    providers=["CPUExecutionProvider"],
                )
                self.input_name = self.session.get_inputs()[0].name
                self.mode = "minifasnet_v2_se"
            except Exception:
                self.session = None
                self.input_name = ""
                self.mode = "fallback"

    def is_available(self) -> bool:
        return self.session is not None and bool(self.input_name)

    def predict_live_score(self, face: np.ndarray) -> tuple[float, dict[str, Any]]:
        if not self.is_available():
            return 0.0, {"mode": self.mode, "reason": "model_unavailable"}

        input_tensor = preprocess_spoof_face(face)
        outputs = self.session.run(None, {self.input_name: input_tensor})
        logits = np.array(outputs[0][0], dtype=np.float32)
        probabilities = softmax(logits)
        live_score = float(probabilities[1])
        spoof_score = float(probabilities[0])
        return live_score, {
            "mode": self.mode,
            "liveProbability": round(live_score, 4),
            "spoofProbability": round(spoof_score, 4),
        }


anti_spoof_engine = AntiSpoofEngine()


def decode_base64_image(value: str) -> np.ndarray | None:
    raw = strip_base64_prefix(value)
    if not raw:
      return None
    try:
        binary = base64.b64decode(raw)
        array = np.frombuffer(binary, dtype=np.uint8)
        return cv2.imdecode(array, cv2.IMREAD_COLOR)
    except Exception:
        return None


def strip_base64_prefix(value: str) -> str:
    text = str(value or "").strip()
    if text.startswith("data:") and "," in text:
        return text.split(",", 1)[1]
    return text


def softmax(values: np.ndarray) -> np.ndarray:
    shifted = values - np.max(values)
    exp_values = np.exp(shifted)
    return exp_values / np.sum(exp_values)


def load_reference_image(request: VerifyRequest) -> np.ndarray | None:
    if request.referenceImageBase64:
        return decode_base64_image(request.referenceImageBase64)

    if not request.referenceImageUrl:
        return None

    try:
        response = requests.get(request.referenceImageUrl, timeout=15)
        response.raise_for_status()
        array = np.frombuffer(response.content, dtype=np.uint8)
        return cv2.imdecode(array, cv2.IMREAD_COLOR)
    except Exception:
        return None


def focus_score(image: np.ndarray) -> float:
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    return float(cv2.Laplacian(gray, cv2.CV_64F).var())


def brightness_score(image: np.ndarray) -> float:
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    return float(np.mean(gray))


def fallback_similarity(face_a: np.ndarray, face_b: np.ndarray) -> float:
    a = preprocess_face(face_a)
    b = preprocess_face(face_b)

    a_flat = a.flatten()
    b_flat = b.flatten()
    a_norm = np.linalg.norm(a_flat)
    b_norm = np.linalg.norm(b_flat)
    if a_norm == 0 or b_norm == 0:
        return 0.0

    cosine = float(np.dot(a_flat, b_flat) / (a_norm * b_norm))

    hist_a = cv2.calcHist([a], [0], None, [64], [0, 256])
    hist_b = cv2.calcHist([b], [0], None, [64], [0, 256])
    cv2.normalize(hist_a, hist_a)
    cv2.normalize(hist_b, hist_b)
    hist_score = float(cv2.compareHist(hist_a, hist_b, cv2.HISTCMP_CORREL))

    combined = (max(-1.0, min(1.0, cosine)) + max(-1.0, min(1.0, hist_score))) / 2.0
    normalized = (combined + 1.0) / 2.0
    return float(max(0.0, min(1.0, normalized)))


def preprocess_face(face: np.ndarray) -> np.ndarray:
    gray = cv2.cvtColor(face, cv2.COLOR_BGR2GRAY)
    resized = cv2.resize(gray, (112, 112))
    return cv2.equalizeHist(resized)


def preprocess_spoof_face(face: np.ndarray) -> np.ndarray:
    rgb = cv2.cvtColor(face, cv2.COLOR_BGR2RGB)
    resized = cv2.resize(rgb, (128, 128))
    normalized = resized.astype(np.float32) / 255.0
    chw = np.transpose(normalized, (2, 0, 1))
    return np.expand_dims(chw, axis=0)


def challenge_liveness_score(meta: dict[str, Any]) -> float:
    checks = [
        bool(meta.get("blinkDetected")),
        bool(meta.get("headTurnLeftDetected") or meta.get("headTurnDetected")),
        bool(meta.get("headTurnRightDetected") or meta.get("headTurnDetected")),
        bool(meta.get("smileDetected") or meta.get("mouthOpenDetected")),
    ]
    score = 0.35
    if checks[0]:
        score += 0.30
    if checks[1] or checks[2]:
        score += 0.25
    if checks[3]:
        score += 0.10
    return float(min(1.0, score))


def capture_quality_band(image_score: float, focus: float, brightness: float) -> str:
    if image_score >= 0.82 and focus >= MIN_FOCUS_SCORE * 2.2 and MIN_BRIGHTNESS <= brightness <= MAX_BRIGHTNESS:
        return "excellent"
    if image_score >= 0.62:
        return "good"
    if image_score >= 0.45:
        return "usable"
    return "poor"


def confidence_band(similarity: float, liveness_score: float) -> str:
    combined = (0.55 * similarity) + (0.45 * liveness_score)
    if combined >= 0.92:
        return "very_high"
    if combined >= 0.84:
        return "high"
    if combined >= 0.76:
        return "medium"
    return "low"


def effective_thresholds(
    challenge_score: float,
    image_score: float,
    spoof_model_score: float,
) -> tuple[float, float]:
    similarity_threshold = FACE_SIMILARITY_THRESHOLD
    liveness_threshold = LIVENESS_SCORE_THRESHOLD

    # Small adaptive relaxation only when the frame and liveness evidence are already strong.
    if challenge_score >= 0.90 and image_score >= 0.72 and spoof_model_score >= 0.90:
        similarity_threshold = max(FACE_SIMILARITY_SOFT_FLOOR, FACE_SIMILARITY_THRESHOLD - 0.02)

    if spoof_model_score >= 0.94 and image_score >= 0.70:
        liveness_threshold = max(LIVENESS_SCORE_SOFT_FLOOR, LIVENESS_SCORE_THRESHOLD - 0.03)

    return similarity_threshold, liveness_threshold


def classify_failure_reason(
    similarity: float,
    liveness_score: float,
    challenge_score: float,
    image_score: float,
    spoof_model_score: float,
    challenge_meta: dict[str, Any],
) -> tuple[str, str]:
    blink_detected = bool(challenge_meta.get("blinkDetected"))
    head_turn_detected = bool(
        challenge_meta.get("headTurnLeftDetected")
        or challenge_meta.get("headTurnRightDetected")
        or challenge_meta.get("headTurnDetected")
    )

    if similarity < FACE_SIMILARITY_THRESHOLD and liveness_score >= LIVENESS_SCORE_THRESHOLD:
        return (
            "FACE_MISMATCH",
            "Kamera qarşısında vəsiqə sahibinin özü olmalıdır.",
        )

    if not blink_detected or not head_turn_detected:
        return (
            "CHALLENGE_INCOMPLETE",
            "Göz qırpma və baş çevirmə addımlarını tam yerinə yetirin.",
        )

    if anti_spoof_engine.is_available() and spoof_model_score < 0.50:
        return (
            "SPOOF_SUSPECTED",
            "Canlı üz skanı tələb olunur. Ekran, foto və ya başqa cihaz göstərməyin.",
        )

    if image_score < 0.45:
        return (
            "LOW_IMAGE_QUALITY",
            "Üzü daha işıqlı və stabil çərçivədə saxlayaraq yenidən yoxlayın.",
        )

    if liveness_score < LIVENESS_SCORE_THRESHOLD:
        return (
            "LIVENESS_FAILED",
            "Canlılıq təsdiqlənmədi. Kameraya yaxınlaşıb challenge addımlarını yenidən edin.",
        )

    return (
        "VERIFICATION_FAILED",
        "Üz doğrulaması tamamlanmadı. Yenidən cəhd edin.",
    )


def image_quality_liveness_score(face: np.ndarray) -> tuple[float, dict[str, Any]]:
    focus = focus_score(face)
    brightness = brightness_score(face)

    focus_part = min(1.0, focus / max(MIN_FOCUS_SCORE, 1.0))
    brightness_distance = abs(brightness - 128.0) / 128.0
    brightness_part = max(0.0, 1.0 - brightness_distance)

    if brightness < MIN_BRIGHTNESS or brightness > MAX_BRIGHTNESS:
        brightness_part *= 0.55

    score = 0.55 * focus_part + 0.45 * brightness_part
    return float(max(0.0, min(1.0, score))), {
        "focusScore": round(focus, 4),
        "brightnessScore": round(brightness, 4),
    }


def select_best_capture(capture_images: list[str]) -> tuple[np.ndarray | None, dict[str, Any]]:
    best_face = None
    best_debug: dict[str, Any] = {"selectedFrameIndex": -1, "detector": engine.mode}
    best_rank = -1.0

    for index, image_string in enumerate(capture_images):
        image = decode_base64_image(image_string)
        if image is None:
            continue
        face, debug = engine.extract_face(image)
        if face is None:
            continue
        quality = focus_score(face)
        if quality > best_rank:
            best_rank = quality
            best_face = face
            best_debug = {
                **debug,
                "selectedFrameIndex": index,
                "qualityScore": round(quality, 4),
            }

    return best_face, best_debug


@app.get("/health")
def health() -> dict[str, Any]:
    return {
        "ok": True,
        "service": "atu-face-verification",
        "version": "v1",
        "mode": engine.mode,
        "antiSpoofMode": anti_spoof_engine.mode,
        "thresholds": {
            "faceSimilarity": FACE_SIMILARITY_THRESHOLD,
            "livenessScore": LIVENESS_SCORE_THRESHOLD,
        },
    }


@app.post("/verify")
def verify(request: VerifyRequest) -> dict[str, Any]:
    reference_image = load_reference_image(request)
    if reference_image is None:
        return {
            "verified": False,
            "faceSimilarity": 0.0,
            "livenessScore": 0.0,
            "antiSpoofLabel": "unknown",
            "debug": {
                "reason": "reference_image_missing",
                "mode": engine.mode,
            },
        }

    reference_face, reference_debug = engine.extract_face(reference_image)
    if reference_face is None:
        return {
            "verified": False,
            "faceSimilarity": 0.0,
            "livenessScore": 0.0,
            "antiSpoofLabel": "unknown",
            "debug": {
                "reason": "reference_face_not_detected",
                "reference": reference_debug,
                "mode": engine.mode,
            },
        }

    capture_face, capture_debug = select_best_capture(request.captureImages)
    if capture_face is None:
        return {
            "verified": False,
            "faceSimilarity": 0.0,
            "livenessScore": 0.0,
            "antiSpoofLabel": "unknown",
            "debug": {
                "reason": "capture_face_not_detected",
                "capturesCount": len(request.captureImages),
                "mode": engine.mode,
            },
        }

    similarity = engine.similarity(reference_face, capture_face)
    challenge_score = challenge_liveness_score(request.challengeMeta)
    image_score, image_debug = image_quality_liveness_score(capture_face)
    spoof_model_score, spoof_debug = anti_spoof_engine.predict_live_score(capture_face)
    if anti_spoof_engine.is_available():
        liveness_score = float((0.70 * spoof_model_score) + (0.20 * challenge_score) + (0.10 * image_score))
    else:
        liveness_score = float((0.65 * challenge_score) + (0.35 * image_score))
    similarity_threshold, liveness_threshold = effective_thresholds(
        challenge_score=challenge_score,
        image_score=image_score,
        spoof_model_score=spoof_model_score,
    )
    verified = similarity >= similarity_threshold and liveness_score >= liveness_threshold
    anti_spoof_label = "live" if liveness_score >= liveness_threshold else "spoof_suspected"
    quality_band = capture_quality_band(
        image_score=image_score,
        focus=float(image_debug["focusScore"]),
        brightness=float(image_debug["brightnessScore"]),
    )
    confidence = confidence_band(similarity=similarity, liveness_score=liveness_score)
    failure_reason, recommended_action = classify_failure_reason(
        similarity=similarity,
        liveness_score=liveness_score,
        challenge_score=challenge_score,
        image_score=image_score,
        spoof_model_score=spoof_model_score,
        challenge_meta=request.challengeMeta,
    )

    return {
        "verified": verified,
        "faceSimilarity": round(similarity, 4),
        "livenessScore": round(liveness_score, 4),
        "antiSpoofLabel": anti_spoof_label,
        "confidenceBand": confidence,
        "captureQualityBand": quality_band,
        "retryable": not verified and failure_reason != "SPOOF_SUSPECTED",
        "failureReason": "" if verified else failure_reason,
        "recommendedAction": "" if verified else recommended_action,
        "debug": {
            "mode": engine.mode,
            "reference": reference_debug,
            "capture": capture_debug,
            "challengeScore": round(challenge_score, 4),
            "imageQualityScore": round(image_score, 4),
            "spoofModelScore": round(spoof_model_score, 4),
            "antiSpoofMode": anti_spoof_engine.mode,
            "thresholds": {
                "faceSimilarity": FACE_SIMILARITY_THRESHOLD,
                "livenessScore": LIVENESS_SCORE_THRESHOLD,
                "effectiveFaceSimilarity": round(similarity_threshold, 4),
                "effectiveLiveness": round(liveness_threshold, 4),
                "softFaceFloor": FACE_SIMILARITY_SOFT_FLOOR,
                "softLivenessFloor": LIVENESS_SCORE_SOFT_FLOOR,
            },
            **image_debug,
            **spoof_debug,
        },
    }


if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=int(os.getenv("PORT", "8090")))
