# ATU Campus Face Verification v1

Bu qovluq üz tanıma və canlılıq yoxlaması üçün ayrıca inference moduludur.

## Məqsəd

Node.js backend auth sessiyasını idarə edir, bu modul isə ML inference hissəsini
ayrıca servisdə saxlayır:

- face verification
- anti-spoof / liveness
- score hesablanması

## Planlanan struktur

- `service.py` - FastAPI/uvicorn giriş nöqtəsi
- `requirements.txt` - Python asılılıqları
- gələcəkdə:
  - `pipeline/`
  - `models/`
  - `utils/`

## V1 model ailələri

- Face verification: `SFace`
- Anti-spoof baseline: `MiniFASNet`
- Android challenge engine: `MediaPipe Face Landmarker`

## Hazırkı vəziyyət

Hazırkı `service.py` artıq minimum işlək v1 axını verir:

1. reference şəkli qəbul edir
2. capture frame-lərdən ən yaxşı üz kadrını seçir
3. similarity score çıxarır
4. challenge meta və image quality ilə liveness score çıxarır
5. verified / failed qərarı qaytarır

## İş rejimi

Servis 2 rejimdə işləyə bilər:

### 1. `fallback`

Default rejimdir. Heç bir əlavə model faylı verilməsə belə işləyir:

- Haar cascade ilə üz tapır
- sadə face crop comparator ilə similarity hesablayır
- challenge meta + blur/brightness ilə liveness score verir

Bu rejim MVP üçündür.

### 2. `sface`

Əgər aşağıdakı model path-ləri verilsə:

- `SFACE_DETECTOR_MODEL_PATH`
- `SFACE_RECOGNIZER_MODEL_PATH`

və OpenCV həmin API-ləri dəstəkləsə, servis `SFace` rejiminə keçir.

### 3. `MiniFASNet V2 SE`

Bu versiyada anti-spoof score üçün real ONNX model xətti də əlavə olunub:

- default model: `backend/face_verification/models/best_model_quantized.onnx`
- arxitektura: `MiniFASNet V2 SE`
- input: `128x128 RGB`
- output: `2 class logits (spoof / live)`

Əgər model faylı mövcuddursa:

- `livenessScore` artıq əsasən modeldən gəlir
- challenge meta və image quality sadəcə əlavə dəstək siqnalı kimi istifadə olunur

## Environment variables

- `PORT=8090`
- `FACE_SIMILARITY_THRESHOLD=0.75`
- `LIVENESS_SCORE_THRESHOLD=0.90`
- `FACE_MIN_FOCUS_SCORE=45`
- `FACE_MIN_BRIGHTNESS=35`
- `FACE_MAX_BRIGHTNESS=220`
- `SFACE_DETECTOR_MODEL_PATH=...`
- `SFACE_RECOGNIZER_MODEL_PATH=...`
- `MINIFAS_MODEL_PATH=...`

## Lokal işə salma

```bash
pip install -r backend/face_verification/requirements.txt
python backend/face_verification/service.py
```

və ya PowerShell ilə:

```powershell
powershell -ExecutionPolicy Bypass -File tools\start-face-verification.ps1
```
