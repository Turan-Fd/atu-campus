# ATU Campus Face Verification v1 Architecture

## Məqsəd

Hazırkı tələbə nömrəsi əsaslı giriş axınını daha təhlükəsiz hala gətirmək üçün
Face Verification v1 əlavə olunur. Yeni axında yalnız tələbə vəsiqəsi nömrəsini
bilən şəxs sistemə daxil ola bilməməlidir. İstifadəçi aşağıdakı addımlardan keçməlidir:

1. Tələbə vəsiqəsi nömrəsini daxil edir.
2. Backend uyğun tələbəni tapır və üz yoxlama sessiyası yaradır.
3. Android tətbiqi canlı kamera yoxlaması və sadə challenge axını icra edir.
4. Çəkilmiş kadrlar/reference şəkil inference servisində yoxlanılır.
5. Uyğunluq keçərsə giriş tamamlanır və yalnız bundan sonra profil məlumatları açılır.

## Təhlükəsizlik problemi

Cari modeldə başqa tələbənin nömrəsini bilmək kifayətdir. Bu isə:

- başqa tələbənin şəxsi məlumatlarına baxmaq,
- başqa profil ilə tətbiqə daxil olmaq,
- gələcəkdə pass və ya qrup funksiyalarından sui-istifadə etmək

kimi risklər yaradır.

## V1 üçün nəyi hədəfləyirik

Face Verification v1 aşağıdakı hücumlardan qorunmağa çalışacaq:

- başqa şəxsin tələbə nömrəsi ilə giriş cəhdi,
- sadə foto göstərərək giriş,
- ekran üzərindən replay tipli sadə spoof cəhdi,
- statik üz görüntüsü ilə saxta giriş.

V1 aşağıdakı səviyyəni hələ tam bağlamır:

- çox güclü deepfake/video injection,
- rooted cihaz üzərindən kamera axınının saxtalaşdırılması,
- yüksək keyfiyyətli maska əsaslı spoof hücumları.

Bu səbəbdən V1 "minimum işlək təhlükəsizlik yüksəlişi" kimi qəbul edilir.

## Tövsiyə olunan ümumi arxitektura

Üç qatlı model:

### 1. Android tətbiqi

Rol:

- CameraX ilə canlı preview və capture
- istifadəçiyə challenge göstərmək
- üz landmark-ları ilə challenge tamamlanmasını izləmək
- backend ilə session əsaslı işləmək

Android tərəfində əsas komponentlər:

- `CameraX`
- `MediaPipe Face Landmarker`
- mövcud login flow-un yeni secure variantı

### 2. Node.js backend

Rol:

- auth sessiyasını yaratmaq
- tələbə məlumatını tapmaq
- reference foto URL/path həll etmək
- Python inference servisindən nəticə almaq
- son qərarı vermək
- audit log saxlamaq

Backend "source of truth" olaraq qalır.

### 3. Python inference service

Rol:

- üz embedding çıxarmaq
- similarity score hesablamaq
- anti-spoof/liveness score çıxarmaq
- yekun risk nəticəsini backend-ə vermək

Bu servis ayrıca saxlanılır ki:

- model dəyişmək asan olsun,
- Node backend sadə qalsın,
- GPU/CPU inference ayrıca idarə olunsun.

## Seçilən open-source model ailələri

### A. Challenge və landmark izləmə: MediaPipe Face Landmarker

İstifadə səbəbi:

- Android-də stabil işləyir
- real-time landmark verir
- blink, head turn, face orientation kimi sadə challenge-lər qurmağa imkan yaradır
- on-device işlədiyi üçün UX sürətlidir

İstifadə sahəsi:

- "göz qırp"
- "başını sola çevir"
- "başını yuxarı qaldır"

Qeyd:
Bu model tam security liveness model deyil, challenge orchestration üçündür.

### B. Face verification: OpenCV Zoo SFace

Default seçim kimi tövsiyə edilir.

Səbəblər:

- Apache 2.0 lisensiya xətti daha rahatdır
- ONNX formatı ilə rahat deploy olunur
- CPU inference üçün münasibdir
- kommersiya / final məhsul üçün hüquqi riskləri bəzi alternativlərdən azdır

V1 üçün əsas embedding modeli kimi seçilir.

### C. Anti-spoof baseline: Silent-Face-Anti-Spoofing (MiniFASNet ailəsi)

Səbəblər:

- Apache 2.0
- açıq anti-spoof baseline-dir
- Android deploy nümunələri mövcuddur
- sadə spoof hücumlarına qarşı V1 səviyyəsində yaxşı başlanğıcdır

Qeyd:
Bu hissə kalibrasiya tələb edəcək. İlk mərhələdə konservativ threshold seçilməlidir.

## Niyə InsightFace default seçim deyil

Texniki olaraq güclüdür, amma pretrained recognition modellərinin lisenziyası və
istifadə şərtləri final məhsul üçün əlavə hüquqi yoxlama tələb edə bilər.

Repo səviyyəsində default qərar:

- verification üçün `SFace`
- anti-spoof üçün `MiniFASNet`
- challenge üçün `MediaPipe`

## Sessiya axını

### Addım 1: Student number daxil edilir

Android:

- tələbə nömrəsini backend-ə göndərir

Backend:

- tələbəni datada axtarır
- uyğun tələbənin reference fotosunu tapır
- `faceAuthSessionId` yaradır

Response:

- `sessionId`
- challenge tipi
- tələbənin minimal public onboarding məlumatı

### Addım 2: Face challenge başlayır

Android:

- ön kamera açılır
- challenge mərhələsi göstərilir
- landmark-larla progress ölçülür
- uyğun frame-lər capture edilir

### Addım 3: Verification request

Android backend-ə göndərir:

- `sessionId`
- selfi kadr(lar)
- challenge nəticə meta məlumatı

Backend:

- reference şəkli resolve edir
- inference service-ə request atır

Inference service:

- reference embedding çıxarır
- captured face embedding çıxarır
- similarity hesablayır
- anti-spoof score hesablayır
- decision payload qaytarır

### Addım 4: Final qərar

Backend:

- threshold-lara baxır
- pass/fail qərarı verir
- audit yazır
- success olduqda mövcud login/profile flow-a keçir

## Endpoint contract-ları

### Node backend

#### `POST /auth/face/start`

Request:

```json
{
  "studentNumber": "145042"
}
```

Response:

```json
{
  "success": true,
  "sessionId": "face_abc123",
  "challenge": {
    "type": "blink_and_turn_left",
    "expiresInSeconds": 180
  },
  "studentPreview": {
    "fullName": "ŞABAN ÖMƏROV",
    "group": "1324a3"
  }
}
```

#### `POST /auth/face/complete`

Request:

```json
{
  "sessionId": "face_abc123",
  "captures": [
    "base64-image-1",
    "base64-image-2"
  ],
  "challengeMeta": {
    "blinkDetected": true,
    "headTurnLeftDetected": true
  }
}
```

Response:

```json
{
  "success": true,
  "verified": true,
  "matchScore": 0.86,
  "livenessScore": 0.94,
  "student": {
    "id": "145042",
    "fullName": "ŞABAN ÖMƏROV",
    "group": "1324a3"
  }
}
```

### Python inference service

#### `GET /health`

Response:

```json
{
  "ok": true,
  "service": "atu-face-verification",
  "version": "v1"
}
```

#### `POST /verify`

Request:

```json
{
  "referenceImageUrl": "https://...",
  "captureImages": [
    "base64-image-1",
    "base64-image-2"
  ],
  "challengeMeta": {
    "blinkDetected": true,
    "headTurnLeftDetected": true
  }
}
```

Response:

```json
{
  "verified": true,
  "faceSimilarity": 0.86,
  "livenessScore": 0.94,
  "antiSpoofLabel": "live",
  "debug": {
    "selectedFrameIndex": 1
  }
}
```

## Data storage

İlk mərhələdə JSON ilə davam edirik.

Yeni fayllar:

- `backend/data/face-auth-sessions.json`
- `backend/data/face-auth-audit.json`

### `face-auth-sessions.json`

Sessiya state-i:

- session id
- student id/work number
- reference image
- createdAt / expiresAt
- attempt count
- status

### `face-auth-audit.json`

Audit məqsədi ilə:

- kim yoxlanıldı
- nə vaxt yoxlanıldı
- score-lar
- fail səbəbi
- hansı cihaz / app version

Qeyd:
Raw selfie kadrları mümkün qədər qalıcı saxlanmamalıdır. Audit üçün yalnız metadata
və minimal lazım olan nəticələr saxlanmalıdır.

## Threshold-lar (ilk təklif)

V1 üçün başlanğıc threshold:

- `faceSimilarity >= 0.75`
- `livenessScore >= 0.90`
- `maxAttempts = 3`
- `sessionExpiry = 180 seconds`

Bu rəqəmlər real test datası ilə kalibrasiya olunmalıdır.

## Android UX prinsipi

Login UX belə dəyişəcək:

1. tələbə nömrəsi daxil edilir
2. "Üz yoxlamasına başla"
3. ön kamera açılır
4. challenge tamamlanır
5. "Yoxlanılır..."
6. uğurlu olduqda giriş edilir

Qayda:

- üz yoxlama keçmədən profil məlumatları açılmır
- tələbə haqqında detallı data challenge başlamamış tam göstərilmir

## Implementasiya mərhələləri

### Phase 1

- arxitektura sənədi
- repo scaffold
- Python service stub
- backend session storage faylları

### Phase 2

- backend `start/complete` endpoint-ləri
- student photo resolve logic
- audit writing

### Phase 3

- Android secure face flow
- CameraX + MediaPipe challenge UI

### Phase 4

- SFace inference inteqrasiyası
- anti-spoof inteqrasiyası
- threshold tuning

### Phase 5

- rollout guard
- retry logic
- fail-safe UX

## Yekun qərar

Repo üçün default texniki seçim:

- **Challenge / landmark:** MediaPipe Face Landmarker
- **Face verification:** OpenCV Zoo SFace
- **Anti-spoof baseline:** Silent-Face-Anti-Spoofing / MiniFASNet
- **Backend orchestrator:** mövcud Node.js backend
- **Inference runtime:** ayrıca Python service

Bu yanaşma MVP-dən final app-a keçid üçün ən balanslı open-source başlanğıcdır:

- hüquqi risk nisbətən aşağı
- CPU-də quraşdırmaq mümkündür
- Android UX-i premium saxlamaq olar
- security səviyyəsi student-number-only girişdən xeyli güclü olur

