package com.atu.campus.services

import android.content.Context
import android.graphics.Bitmap
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.components.containers.Category
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import kotlin.math.max
import kotlin.math.min

data class FaceChallengeState(
    val available: Boolean,
    val facePresent: Boolean,
    val blinkDetected: Boolean,
    val headTurnLeftDetected: Boolean,
    val confidence: Float,
    val message: String = ""
)

class FaceChallengeAnalyzer(
    private val context: Context
) {
    private val modelAssetName = "face_landmarker.task"

    private val faceLandmarker: FaceLandmarker? by lazy {
        try {
            if (context.assets.list("")?.contains(modelAssetName) != true) return@lazy null
            val baseOptions = BaseOptions.builder()
                .setModelAssetPath(modelAssetName)
                .build()
            val options = FaceLandmarker.FaceLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setRunningMode(RunningMode.IMAGE)
                .setNumFaces(1)
                .setMinFaceDetectionConfidence(0.5f)
                .setMinFacePresenceConfidence(0.5f)
                .setMinTrackingConfidence(0.5f)
                .setOutputFaceBlendshapes(true)
                .build()
            FaceLandmarker.createFromOptions(context, options)
        } catch (_: Exception) {
            null
        }
    }

    fun isAvailable(): Boolean = faceLandmarker != null

    fun analyze(bitmap: Bitmap): FaceChallengeState {
        val landmarker = faceLandmarker ?: return FaceChallengeState(
            available = false,
            facePresent = false,
            blinkDetected = false,
            headTurnLeftDetected = false,
            confidence = 0f,
            message = "MediaPipe model asset tapılmadı."
        )

        return try {
            val mpImage = BitmapImageBuilder(bitmap).build()
            val result = landmarker.detect(mpImage)
            val landmarks = result.faceLandmarks().firstOrNull()
            if (landmarks.isNullOrEmpty()) {
                return FaceChallengeState(
                    available = true,
                    facePresent = false,
                    blinkDetected = false,
                    headTurnLeftDetected = false,
                    confidence = 0f,
                    message = "Üz kadrda görünmür."
                )
            }

            val classifications: List<Category> = if (result.faceBlendshapes().isPresent) {
                val groups: List<List<Category>> = result.faceBlendshapes().get().map { rawGroup ->
                    rawGroup.map { it as Category }
                }
                groups.firstOrNull().orEmpty()
            } else {
                emptyList()
            }
            val blinkLeft = classifications.firstOrNull { it.categoryName() == "eyeBlinkLeft" }?.score() ?: 0f
            val blinkRight = classifications.firstOrNull { it.categoryName() == "eyeBlinkRight" }?.score() ?: 0f
            val blinkDetected = blinkLeft >= 0.45f || blinkRight >= 0.45f

            val xs = landmarks.map { it.x() }
            val minX = xs.minOrNull() ?: 0f
            val maxX = xs.maxOrNull() ?: 1f
            val width = max(0.001f, maxX - minX)
            val noseX = landmarks.getOrNull(1)?.x() ?: ((minX + maxX) / 2f)
            val normalizedNoseX = (noseX - minX) / width
            val headTurnLeftDetected = normalizedNoseX >= 0.60f

            val confidence = min(
                1f,
                max(
                    blinkLeft,
                    blinkRight
                ) + if (headTurnLeftDetected) 0.25f else 0f
            )

            FaceChallengeState(
                available = true,
                facePresent = true,
                blinkDetected = blinkDetected,
                headTurnLeftDetected = headTurnLeftDetected,
                confidence = confidence,
                message = ""
            )
        } catch (_: Exception) {
            FaceChallengeState(
                available = false,
                facePresent = false,
                blinkDetected = false,
                headTurnLeftDetected = false,
                confidence = 0f,
                message = "Üz analizi başladılmadı."
            )
        }
    }

    fun close() {
        faceLandmarker?.close()
    }
}
