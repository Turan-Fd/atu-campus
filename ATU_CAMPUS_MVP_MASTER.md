# ATU Campus MVP Master File

Bu fayl layihənin indiki MVP vəziyyətini bir yerdə toplamaq üçündür. Məqsəd odur ki, app, backend, data, API, build, deploy və gələcək keçid planı bir sənəddə olsun.

## 1. Layihə adı

- Məhsul: `ATU Campus`
- Platforma:
  - Android: native `Kotlin + Jetpack Compose + Material 3`
  - iOS: ayrıca `SwiftUI` skeleti mövcuddur
- Backend: `Node.js`
- Data mərhələsi: `JSON-based MVP`

## 2. MVP məqsədi

ATU tələbəsi üçün vahid mobil giriş nöqtəsi yaratmaq:

- tələbə giriş/onboarding
- tələbə kartı / nömrə ilə verifikasiya
- tələbə profilinin lokaldakı saxlanması
- xəbər/elan/tədbir axını
- AI köməkçi
- ATU Pass demo keçid axını
- qrup və rəsmi chat otaqları
- adminlər üçün content və bildiriş göndərilməsi

## 3. Hazır MVP funksiyaları

### Tələbə tərəfi

- Splash screen
- Permission screen
- Student access screen
- Kart / tələbə nömrəsi əsasında giriş
- Backend student verification
- Local profile storage
- Home dashboard
- Search screen
- News detail screen
- Notifications screen
- AI assistant chat
- ATU Pass demo screen
- Profile screen
- Chat room list
- Group chat / official room chat
- Voice message göndərilməsi
- Attachment göndərilməsi və açılması
- Dark / Light mode

### Admin tərəfi

- `News Admin` login və panel
- `SMS Admin` login və panel
- Content publish:
  - xəbər
  - elan
  - tədbir
- Official group paylaşımı
- Birbaşa bildiriş göndərilməsi
- Tələbə axtarışı və seçimi

### Push / notification tərəfi

- Firebase Messaging inteqrasiyası üçün Android tərəfdə hazırlıq
- FCM token sync servisləri
- Local notification flow
- Notification detail açılışı

## 4. Texnologiya stack

### Android

- Kotlin
- Jetpack Compose
- Material 3
- CameraX
- ML Kit Text Recognition
- Coil
- Kotlin Coroutines
- WorkManager
- Firebase Messaging

### Backend

- Node.js `>=20`
- `firebase-admin`
- file-based JSON persistence

### iOS

- SwiftUI
- ayrıca `ios/ATUCampus/` skeleti

## 5. Android build konfiqurasiyası

Fayl:
- [app/build.gradle.kts](C:\Users\Admin\Documents\New project 19\app\build.gradle.kts)

Əsas dəyərlər:

- `applicationId = "com.atu.campus"`
- `minSdk = 26`
- `targetSdk = 36`
- `compileSdk = 36`
- `versionCode = 27`
- `versionName = "3.7"`
- Java/Kotlin target: `17`

## 6. Android icazələri

Fayl:
- [AndroidManifest.xml](C:\Users\Admin\Documents\New project 19\app\src\main\AndroidManifest.xml)

Hazır icazələr:

- `android.permission.INTERNET`
- `android.permission.POST_NOTIFICATIONS`
- `android.permission.RECORD_AUDIO`

Qeyd:
- əvvəlki card scan axınında kamera əsas mövzu idi; hazır manifest snapshot-da `CAMERA` icazəsi görünmür, bunu yenidən aktiv etmək lazım ola bilər əgər scan axını yenidən əsas giriş üsulu olacaqsa.

## 7. Android əsas source strukturu

### Entry

- [MainActivity.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\MainActivity.kt)

### Navigation

- [AppNavigation.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\navigation\AppNavigation.kt)
- [Screen.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\navigation\Screen.kt)

### Main screens

- [SplashScreen.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\ui\screens\SplashScreen.kt)
- [PermissionScreen.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\ui\screens\PermissionScreen.kt)
- [StudentAccessScreen.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\ui\screens\StudentAccessScreen.kt)
- [ScanCardScreen.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\ui\screens\ScanCardScreen.kt)
- [ProcessingScreen.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\ui\screens\ProcessingScreen.kt)
- [ConfirmStudentScreen.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\ui\screens\ConfirmStudentScreen.kt)
- [HomeScreen.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\ui\screens\HomeScreen.kt)
- [AdminLoginScreen.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\ui\screens\AdminLoginScreen.kt)
- [NewsAdminScreen.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\ui\screens\NewsAdminScreen.kt)
- [SmsAdminScreen.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\ui\screens\SmsAdminScreen.kt)

### UI system / components

- [PremiumUi.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\ui\components\PremiumUi.kt)
- [AtuButton.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\ui\components\AtuButton.kt)
- [DashboardTile.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\ui\components\DashboardTile.kt)
- [InfoCard.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\ui\components\InfoCard.kt)
- [ProfileField.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\ui\components\ProfileField.kt)
- [ScanOverlay.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\ui\components\ScanOverlay.kt)
- [SecurityBadge.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\ui\components\SecurityBadge.kt)

### Theme

- [Color.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\ui\theme\Color.kt)
- [Theme.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\ui\theme\Theme.kt)
- [Type.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\ui\theme\Type.kt)

### Data models

- [StudentProfile.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\data\StudentProfile.kt)
- [LocalProfileStorage.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\data\LocalProfileStorage.kt)
- [AtuNews.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\data\AtuNews.kt)
- [CampusChatMessage.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\data\CampusChatMessage.kt)
- [CampusChatRoom.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\data\CampusChatRoom.kt)
- [CampusNotificationItem.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\data\CampusNotificationItem.kt)
- [StudentDirectoryEntry.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\data\StudentDirectoryEntry.kt)

### Services

- [BackendStudentService.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\services\BackendStudentService.kt)
- [BackendConfigStore.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\services\BackendConfigStore.kt)
- [BackendHealthService.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\services\BackendHealthService.kt)
- [CampusContentService.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\services\CampusContentService.kt)
- [CampusCommunityService.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\services\CampusCommunityService.kt)
- [CampusNotificationService.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\services\CampusNotificationService.kt)
- [CampusFirebaseMessagingService.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\services\CampusFirebaseMessagingService.kt)
- [FcmTokenSyncService.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\services\FcmTokenSyncService.kt)
- [NotificationSyncWorker.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\services\NotificationSyncWorker.kt)
- [NotificationSyncScheduler.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\services\NotificationSyncScheduler.kt)
- [AiChatService.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\services\AiChatService.kt)
- [OcrService.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\services\OcrService.kt)
- [CameraImageStore.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\services\CameraImageStore.kt)
- [VoiceMessageRecorder.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\services\VoiceMessageRecorder.kt)
- [VoiceMessageEncoder.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\services\VoiceMessageEncoder.kt)
- [AdminSelectedAttachment.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\services\AdminSelectedAttachment.kt)
- [AdminImageEncoder.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\services\AdminImageEncoder.kt)
- [SecurityService.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\services\SecurityService.kt)

## 8. Backend ümumi təsvir

Əsas fayl:
- [backend/server.js](C:\Users\Admin\Documents\New project 19\backend\server.js)

Backend hazırda:

- `Node.js` üzərində işləyir
- JSON fayllarla persist edir
- tələbə verifikasiyası edir
- news / announcement / event content saxlayır
- notifications saxlayır
- chat rooms və chat messages saxlayır
- admin login idarə edir
- FCM üçün token saxlayır
- OpenAI backend proxy kimi işləyir

## 9. Backend ENV dəyişənləri

Real secret dəyərlər bu fayla yazılmır. Yalnız lazım olan adlar:

- `PORT`
- `OPENAI_API_KEY`
- `OPENAI_MODEL`
- `ATU_NEWS_ADMIN_CODE`
- `ATU_SMS_ADMIN_CODE`
- `ATU_NEWS_ADMIN_PASSWORD`
- `FIREBASE_SERVICE_ACCOUNT_PATH`
- `FIREBASE_PROJECT_ID`
- `STUDENT_PHOTOS_DIR`

## 10. Backend default admin girişləri

`backend/server.js` üzrə hazır default dəyərlər:

- News Admin access code: `1970103`
- SMS Admin access code: `899913`
- Admin password: `ATU@1970`

Qeyd:
- Bunlar MVP/default dəyərlərdir
- production üçün mütləq env üzərindən verilməli və dəyişdirilməlidir

## 11. Backend əsas JSON data faylları

Qovluq:
- `backend/data/`

Fayllar:

- [students.json](C:\Users\Admin\Documents\New project 19\backend\data\students.json)
  - legacy tələbə login datası

- [statistika-students.json](C:\Users\Admin\Documents\New project 19\backend\data\statistika-students.json)
  - əsas tələbə data bazası snapshot-ı
  - work number, ad, soyad, qrup, ixtisas və s.

- [student-photos.json](C:\Users\Admin\Documents\New project 19\backend\data\student-photos.json)
  - tələbə şəkil map-i
  - work number -> local/remote şəkil istinadı

- [campus-content.json](C:\Users\Admin\Documents\New project 19\backend\data\campus-content.json)
  - app daxilində yaradılmış content
  - news / announcement / event

- [campus-notifications.json](C:\Users\Admin\Documents\New project 19\backend\data\campus-notifications.json)
  - göndərilmiş bildirişlər

- [campus-device-tokens.json](C:\Users\Admin\Documents\New project 19\backend\data\campus-device-tokens.json)
  - student id -> device token mapping

- [campus-chat-rooms.json](C:\Users\Admin\Documents\New project 19\backend\data\campus-chat-rooms.json)
  - rəsmi otaq + qrup otaqları

- [campus-chat-messages.json](C:\Users\Admin\Documents\New project 19\backend\data\campus-chat-messages.json)
  - bütün chat mesajları

## 12. Backend upload/storage məntiqi

Backend bu mərhələdə:

- JSON-ları diskdə saxlayır
- əlavə media üçün `backend/data/campus-uploads/` istifadə edir
- tələbə fotoları üçün:
  - local qovluq
  - və ya `Cloudinary` URL mapping

## 13. Student photo məntiqi

Kod logikasına görə:

- tələbə şəkli `student-photos.json` ilə xəritələnir
- `workNumber` normalize edilir
- əgər mapping URL-dirsə birbaşa qaytarılır
- əks halda local foto qovluğundan public endpoint ilə verilir

Əlaqəli fayllar:

- [student-photos.json](C:\Users\Admin\Documents\New project 19\backend\data\student-photos.json)
- [upload_student_photos_cloudinary.py](C:\Users\Admin\Documents\New project 19\backend\scripts\upload_student_photos_cloudinary.py)
- [upload-student-photos-cloudinary.ps1](C:\Users\Admin\Documents\New project 19\tools\upload-student-photos-cloudinary.ps1)

## 14. Backend endpoint xəritəsi

Repo və hazır axınlara əsasən MVP backend aşağıdakı əsas endpoint-ləri daşıyır:

### Health

- `GET /health`

### Student

- `GET /students?query=...`
- `POST /verify-card`

### Admin auth

- `POST /admin/login`

### Campus content

- `GET /campus-content`
- `POST /admin/content`

### Notifications

- `GET /notifications?studentId=...`
- `POST /admin/direct-notification`

### Chat

- `GET /chat/rooms?studentId=...`
- `GET /chat/messages?roomId=...&since=...`
- `POST /chat/message`
- `POST /chat/reaction`
- `POST /chat/message-action`

### Device token / FCM

- token sync endpoint-ləri app flow içində istifadə olunur

### Upload/public access

- `GET /student-photo/:workNumber`
- `GET /campus-upload/:fileName`

### AI

- backend OpenAI proxy məntiqi istifadə edir

## 15. OpenAI inteqrasiyası

Kod:
- [AiChatService.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\services\AiChatService.kt)
- [backend/server.js](C:\Users\Admin\Documents\New project 19\backend\server.js)

Model:

- Render konfiqində default:
  - `OPENAI_MODEL = gpt-4o-mini`

Məntiq:

- Android app birbaşa OpenAI-a getmir
- app backend-ə sorğu göndərir
- backend `OPENAI_API_KEY` ilə cavab alır

## 16. Firebase / FCM vəziyyəti

Android tərəfdə support mövcuddur:

- Firebase Messaging dependency var
- `CampusFirebaseMessagingService` var
- token sync servisləri var

Real production push üçün lazım olanlar:

- `app/google-services.json`
- backend üçün service account JSON
- `FIREBASE_PROJECT_ID`
- `FIREBASE_SERVICE_ACCOUNT_PATH`

## 17. OCR / scan vəziyyəti

Kod:

- [ScanCardScreen.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\ui\screens\ScanCardScreen.kt)
- [OcrService.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\services\OcrService.kt)
- [ProcessingScreen.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\ui\screens\ProcessingScreen.kt)
- [ConfirmStudentScreen.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\ui\screens\ConfirmStudentScreen.kt)

Status:

- CameraX və OCR əsaslı axın kodda mövcuddur
- hazır məhsul axınında tələbə nömrəsi / backend verification daha çox istifadə olunur
- giriş strategiyası sonradan tam student-number-first modelə keçə bilər

## 18. Local storage

Kod:

- [LocalProfileStorage.kt](C:\Users\Admin\Documents\New project 19\app\src\main\java\com\atu\campus\data\LocalProfileStorage.kt)

Saxlanılan əsas sahələr:

- id
- ad
- soyad
- ata adı
- fin
- şəxsiyyət vəsiqəsi
- qrup
- fakültə
- ixtisas
- təhsil forması
- status
- photoUrl

## 19. Build və run skriptləri

### Android APK build

- [build-apk.ps1](C:\Users\Admin\Documents\New project 19\tools\build-apk.ps1)

İş:

- `:app:assembleDebug`
- APK `dist/` altına kopyalanır

### Backend start

- [start-backend.ps1](C:\Users\Admin\Documents\New project 19\tools\start-backend.ps1)

İş:

- Node tapır
- firewall rule yaratmağa çalışır
- local və LAN URL göstərir
- backend işə salır

### Backend restart

- [restart-backend.ps1](C:\Users\Admin\Documents\New project 19\tools\restart-backend.ps1)

### Telefon install

- [install-phone.ps1](C:\Users\Admin\Documents\New project 19\tools\install-phone.ps1)

### Student data import

- [import-student-data.ps1](C:\Users\Admin\Documents\New project 19\tools\import-student-data.ps1)

İstifadə etdiyi mənbələr:

- `C:\Users\Admin\Downloads\Statistika.xlsx`
- `C:\Users\Admin\Documents\student photos`

### Student photo Cloudinary upload

- [upload-student-photos-cloudinary.ps1](C:\Users\Admin\Documents\New project 19\tools\upload-student-photos-cloudinary.ps1)

## 20. Render deploy vəziyyəti

Konfiq:
- [render.yaml](C:\Users\Admin\Documents\New project 19\render.yaml)

Hazır qeydlər:

- service type: `web`
- runtime: `node`
- name: `atu-campus-backend`
- build command: `npm install`
- start command: `npm start`
- health check: `/health`
- auto deploy: `true`

Default env note:

- `OPENAI_API_KEY` manual secret
- `OPENAI_MODEL = gpt-4o-mini`

## 21. Domain vəziyyəti

Hazır plan:

- `api.atucampus.org` -> backend üçün
- `atucampus.org` / `www.atucampus.org` -> ayrıca website üçün istifadə oluna bilər

Bu struktur app-ə mane olmur, şərt odur ki:

- `api.atucampus.org` DNS yazısı dəyişdirilməsin

## 22. iOS vəziyyəti

iOS qovluğu mövcuddur:

- [ios/ATUCampus](C:\Users\Admin\Documents\New project 19\ios\ATUCampus)

Əsas fayllar:

- [ATUCampusApp.swift](C:\Users\Admin\Documents\New project 19\ios\ATUCampus\ATUCampus\ATUCampusApp.swift)
- [RootView.swift](C:\Users\Admin\Documents\New project 19\ios\ATUCampus\ATUCampus\RootView.swift)
- [Services.swift](C:\Users\Admin\Documents\New project 19\ios\ATUCampus\ATUCampus\Services.swift)

Qeyd:

- bu MVP-nin əsas işlək məhsulu Android tərəfidir
- iOS qovluğu parallel baza/skelet kimi saxlanır

## 23. Cari real risklər / technical debt

### 1. Encoding problemi

Azərbaycanca mətnlərdə müxtəlif ekranlarda `ə/ş/ı/ö/ü` pozulmaları olub və hələ də qalıqları ola bilər.

### 2. JSON-based persistence

Hazırda bütün əsas sistemlər JSON fayllarla işləyir:

- multi-user concurrency üçün zəifdir
- production üçün uyğun deyil

### 3. Admin girişləri

MVP default access code və password ilə işləyir. Production üçün dəyişməlidir.

### 4. FCM tam production setup

Kod var, amma real env və Firebase service account düzgün yerləşdirilməlidir.

### 5. Attachment/document preview

Hazırda in-app viewer mövcuddur, amma müxtəlif cihaz/browser engine fərqlərində ayrıca test lazımdır.

### 6. Chat realtime səviyyəsi

Polling/JSON model var; tam scale üçün websocket və DB lazımdır.

## 24. Production-a keçid üçün tələb olunan böyük dəyişikliklər

### Minimum production keçidi

1. JSON -> real database
2. admin auth gücləndirilməsi
3. media üçün stabil storage
4. full FCM production setup
5. encoding tam təmizlənməsi
6. HTTPS/domain/env sabitləşdirilməsi
7. audit logging
8. role/permission sistemi

### Uyğun stack

- Backend: Node.js
- DB: PostgreSQL
- Media: Cloudinary və ya S3
- Push: Firebase
- Hosting: Render / VPS / digər production servis

## 25. Cari faydalı komandalar

### Android build

```powershell
.\gradlew.bat :app:assembleDebug --no-daemon
```

### APK helper

```powershell
powershell -ExecutionPolicy Bypass -File tools\build-apk.ps1
```

### Backend local start

```powershell
powershell -ExecutionPolicy Bypass -File tools\start-backend.ps1
```

### Backend manual

```powershell
npm install
npm start
```

### Student import

```powershell
powershell -ExecutionPolicy Bypass -File tools\import-student-data.ps1
```

### Student photos Cloudinary upload

```powershell
powershell -ExecutionPolicy Bypass -File tools\upload-student-photos-cloudinary.ps1
```

## 26. Bu sənəddə qəsdən yazılmayan məlumatlar

Təhlükəsizlik üçün bu faylda saxlanmır:

- real API key-lər
- OpenAI secret
- Firebase service account JSON
- Cloudinary API secret
- şəxsi cihaz token-ləri

Bunlar yalnız env və secret file kimi saxlanmalıdır.

## 27. Nəticə

Bu repo hazırda:

- Android üçün real native MVP-dir
- backend-lə işləyən tələbə platforması bazasına sahibdir
- admin content, chat, bildiriş, AI və pass demo axınlarını ehtiva edir
- production-dan əvvəl hələ data, auth, encoding və realtime qatında möhkəmləndirmə istəyir

Bu sənəd MVP snapshot kimi istifadə oluna bilər:

- texniki təqdimat
- komanda onboarding
- gələcək production migration planı
- investor/universitet demo hazırlığı
