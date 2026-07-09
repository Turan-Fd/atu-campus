package com.atu.campus.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FaceRetouchingNatural
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.atu.campus.services.CameraImageStore
import com.atu.campus.services.FaceChallengeAnalyzer
import com.atu.campus.ui.components.AtuInlineNote
import com.atu.campus.ui.components.AtuPrimaryButton
import com.atu.campus.ui.components.AtuStatusBadge
import com.atu.campus.ui.components.atuPressScale
import com.atu.campus.ui.theme.AtuDanger
import com.atu.campus.ui.theme.AtuPrimary
import com.atu.campus.ui.theme.AtuWhite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

private data class FaceChallengeStep(
    val title: String,
    val instruction: String
)

enum class FaceVerificationFeedbackState {
    Idle,
    Success,
    Failure
}

enum class FaceVerificationMode {
    Verify,
    Enroll
}

@Composable
fun FaceVerificationScreen(
    studentPreviewName: String,
    studentPreviewGroup: String,
    mode: FaceVerificationMode,
    imageStore: CameraImageStore,
    loading: Boolean,
    message: String,
    feedbackState: FaceVerificationFeedbackState,
    onBack: () -> Unit,
    onSubmit: (captures: List<String>, blinkDetected: Boolean, headTurnLeftDetected: Boolean) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    val mainExecutor = remember { ContextCompat.getMainExecutor(context) }
    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }
    val challengeAnalyzer = remember { FaceChallengeAnalyzer(context.applicationContext) }

    val steps = remember(mode) {
        listOf(
            FaceChallengeStep("Kadr 1", "Üzünüzü dairənin mərkəzində sabit saxlayın"),
            FaceChallengeStep("Kadr 2", "Canlılıq üçün bir dəfə göz qırpın"),
            FaceChallengeStep(
                "Kadr 3",
                if (mode == FaceVerificationMode.Enroll) {
                    "Yeni referans şəkli yaratmaq üçün başınızı azca sola çevirin"
                } else {
                    "Müqayisə üçün başınızı azca sola çevirin"
                }
            )
        )
    }

    val capturedPaths = remember { mutableStateListOf<String>() }
    var currentStepIndex by remember { mutableIntStateOf(0) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var localError by remember { mutableStateOf("") }
    var permissionMissing by remember { mutableStateOf(false) }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var facePresent by remember { mutableStateOf(false) }
    var blinkDetected by remember { mutableStateOf(false) }
    var headTurnLeftDetected by remember { mutableStateOf(false) }
    var analyzerAvailable by remember { mutableStateOf(challengeAnalyzer.isAvailable()) }
    var analyzerConfidence by remember { mutableFloatStateOf(0f) }
    var autoCaptureInFlight by remember { mutableStateOf(false) }

    val ambient = rememberInfiniteTransition(label = "faceCaptureAmbient")
    val haloPulse by ambient.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(animation = tween(1500), repeatMode = RepeatMode.Reverse),
        label = "haloPulse"
    )
    val scanDrift by ambient.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(1900), repeatMode = RepeatMode.Restart),
        label = "scanDrift"
    )
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        permissionMissing = !granted
        localError = if (granted) "" else "Kamera icazəsi verilmədi."
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionMissing = true
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    DisposableEffect(lifecycleOwner, previewView, hasCameraPermission) {
        if (!hasCameraPermission) {
            imageCapture = null
            onDispose { }
        }
        val providerFuture = ProcessCameraProvider.getInstance(context)
        val listener = Runnable {
            try {
                val cameraProvider = providerFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val capture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_FRONT_CAMERA,
                    preview,
                    capture
                )
                imageCapture = capture
                permissionMissing = false
                localError = ""
            } catch (_: Exception) {
                localError = "Ön kamera açıla bilmədi. Cihaz icazələrini yoxlayın."
            }
        }
        providerFuture.addListener(listener, mainExecutor)
        onDispose {
            challengeAnalyzer.close()
            try {
                providerFuture.get().unbindAll()
            } catch (_: Exception) {
            }
        }
    }

    LaunchedEffect(previewView, loading) {
        if (loading) return@LaunchedEffect
        while (isActive) {
            val snapshot = previewView.bitmap?.copy(Bitmap.Config.ARGB_8888, false)
            if (snapshot != null) {
                val state = withContext(Dispatchers.Default) {
                    challengeAnalyzer.analyze(snapshot)
                }
                analyzerAvailable = state.available
                facePresent = state.facePresent
                blinkDetected = blinkDetected || state.blinkDetected
                headTurnLeftDetected = headTurnLeftDetected || state.headTurnLeftDetected
                analyzerConfidence = state.confidence
                if (!state.available && state.message.isNotBlank()) {
                    localError = state.message
                } else if (state.available && localError == "MediaPipe model asset tapılmadı.") {
                    localError = ""
                }
                if (blinkDetected && currentStepIndex == 1) {
                    currentStepIndex = 2
                }
            }
            delay(700)
        }
    }

    fun captureCurrentStep(triggeredAutomatically: Boolean = false) {
        if (autoCaptureInFlight && !triggeredAutomatically) return
        val capture = imageCapture ?: run {
            localError = "Kamera hələ hazır deyil."
            autoCaptureInFlight = false
            return
        }
        if (currentStepIndex >= 0 && !facePresent) {
            localError = "Üzünüz çərçivənin içində tam görünməlidir."
            autoCaptureInFlight = false
            return
        }
        if (currentStepIndex == 1 && analyzerAvailable && !blinkDetected) {
            localError = "İkinci kadrdan əvvəl bir dəfə göz qırpın."
            autoCaptureInFlight = false
            return
        }
        if (currentStepIndex == 2 && analyzerAvailable && !headTurnLeftDetected) {
            localError = "Son kadr üçün başınızı bir qədər sola çevirin."
            autoCaptureInFlight = false
            return
        }
        autoCaptureInFlight = true
        val file = imageStore.createFaceImageFile()
        capture.takePicture(
            ImageCapture.OutputFileOptions.Builder(file).build(),
            mainExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    autoCaptureInFlight = false
                    if (capturedPaths.size > currentStepIndex) {
                        capturedPaths[currentStepIndex] = file.absolutePath
                    } else {
                        capturedPaths.add(file.absolutePath)
                    }
                    localError = ""
                    if (currentStepIndex < steps.lastIndex) {
                        currentStepIndex += 1
                    } else {
                        onSubmit(capturedPaths.toList(), blinkDetected, headTurnLeftDetected)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    autoCaptureInFlight = false
                    localError = "Kadr çəkilmədi. Yenidən cəhd edin."
                }
            }
        )
    }

    LaunchedEffect(
        analyzerAvailable,
        facePresent,
        blinkDetected,
        headTurnLeftDetected,
        currentStepIndex,
        loading
    ) {
        if (loading || !analyzerAvailable || autoCaptureInFlight) return@LaunchedEffect
        val readyForCurrentStep = when (currentStepIndex) {
            0 -> facePresent
            1 -> blinkDetected
            2 -> headTurnLeftDetected
            else -> false
        }
        val notYetCaptured = capturedPaths.size <= currentStepIndex
        if (readyForCurrentStep && notYetCaptured) {
            delay(650)
            captureCurrentStep(triggeredAutomatically = true)
        }
    }

    val step = steps[currentStepIndex.coerceIn(0, steps.lastIndex)]
    val headline = if (mode == FaceVerificationMode.Enroll) {
        "Yeni üz profili yaradılır"
    } else {
        "Üz doğrulaması"
    }
    val subheadline = if (mode == FaceVerificationMode.Enroll) {
        "Şəkliniz sistemdə yoxdur. Bu canlı skan ilk referans şəkli kimi saxlanacaq."
    } else {
        "Məlumatlarınız təsdiqləndi. İndi canlı üz müqayisəsi aparılır."
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090B11))
    ) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(Color.Black.copy(alpha = 0.42f))

            val frameWidth = size.width * 0.70f
            val frameHeight = frameWidth * 1.18f
            val left = (size.width - frameWidth) / 2f
            val top = (size.height - frameHeight) / 2f - 36.dp.toPx()
            val radius = 42.dp.toPx()

            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(left, top),
                size = Size(frameWidth, frameHeight),
                cornerRadius = CornerRadius(radius, radius),
                blendMode = BlendMode.Clear
            )
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.80f),
                        AtuWhite.copy(alpha = 0.96f),
                        AtuPrimary.copy(alpha = 0.88f)
                    )
                ),
                topLeft = Offset(left, top),
                size = Size(frameWidth, frameHeight),
                cornerRadius = CornerRadius(radius, radius),
                style = Stroke(width = 3.dp.toPx())
            )

            val scanY = top + frameHeight * (0.16f + (0.66f * scanDrift))
            drawLine(
                brush = Brush.horizontalGradient(
                    listOf(Color.Transparent, Color.White, AtuWhite, Color.Transparent)
                ),
                start = Offset(left + 24.dp.toPx(), scanY),
                end = Offset(left + frameWidth - 24.dp.toPx(), scanY),
                strokeWidth = 4.dp.toPx()
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.36f),
                shape = RoundedCornerShape(28.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.14f))
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = headline,
                                color = AtuWhite,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = subheadline,
                                color = AtuWhite.copy(alpha = 0.80f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        AtuStatusBadge(
                            text = "${currentStepIndex + 1}/3",
                            darkMode = true,
                            icon = if (mode == FaceVerificationMode.Enroll) {
                                Icons.Outlined.FaceRetouchingNatural
                            } else {
                                Icons.Outlined.Shield
                            }
                        )
                    }

                    if (studentPreviewName.isNotBlank()) {
                        Text(
                            text = studentPreviewName,
                            color = AtuWhite,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (studentPreviewGroup.isNotBlank()) {
                        Text(
                            text = "$studentPreviewGroup qrupu",
                            color = AtuWhite.copy(alpha = 0.74f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(steps.size) { index ->
                    AtuStatusBadge(
                        text = if (index < capturedPaths.size) "Hazır" else "Gözləyir",
                        darkMode = true,
                        icon = if (index < capturedPaths.size) {
                            Icons.Outlined.CheckCircle
                        } else {
                            Icons.Outlined.FaceRetouchingNatural
                        },
                        success = index < capturedPaths.size
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.34f),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.14f))
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = step.title,
                        color = AtuWhite.copy(alpha = 0.70f),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = step.instruction,
                        color = AtuWhite,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AtuStatusBadge(
                            text = if (facePresent) "Üz göründü" else "Üz gözlənilir",
                            darkMode = true,
                            icon = Icons.Outlined.FaceRetouchingNatural,
                            success = facePresent
                        )
                        AtuStatusBadge(
                            text = if (blinkDetected) "Blink hazır" else "Blink",
                            darkMode = true,
                            icon = Icons.Outlined.CheckCircle,
                            success = blinkDetected
                        )
                        AtuStatusBadge(
                            text = if (headTurnLeftDetected) "Baxış hazır" else "Sola baxın",
                            darkMode = true,
                            icon = Icons.Outlined.CheckCircle,
                            success = headTurnLeftDetected
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .size(88.dp)
                    .graphicsLayer {
                        scaleX = if (loading || autoCaptureInFlight) 0.98f else haloPulse
                        scaleY = if (loading || autoCaptureInFlight) 0.98f else haloPulse
                    }
                    .background(Color.White.copy(alpha = 0.14f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    onClick = { if (!loading) captureCurrentStep() },
                    shape = CircleShape,
                    color = AtuWhite,
                    modifier = Modifier
                        .size(74.dp)
                        .atuPressScale()
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.CameraAlt,
                            contentDescription = "Kadr çək",
                            tint = AtuPrimary,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }

            Text(
                text = when {
                    loading -> "Üz doğrulanır, zəhmət olmasa gözləyin..."
                    analyzerAvailable -> "Uyğun hərəkət aşkar olunanda kadr avtomatik götürüləcək."
                    else -> "Canlılıq analizi hazır olmayanda düymə ilə kadr götürə bilərsiniz."
                },
                color = AtuWhite,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )

            if (analyzerAvailable) {
                Text(
                    text = "Canlılıq analizi aktivdir • etibar ${(analyzerConfidence * 100).toInt()}%",
                    color = AtuWhite.copy(alpha = 0.76f),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (permissionMissing) {
                AtuInlineNote(
                    text = "Kamera icazəsi olmadan üz doğrulaması işləməyəcək.",
                    icon = Icons.Outlined.Lock,
                    darkMode = true
                )
            }

            if (message.isNotBlank() || localError.isNotBlank()) {
                Text(
                    text = if (localError.isNotBlank()) localError else message,
                    color = AtuDanger,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AtuWhite, RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AtuPrimaryButton(
                    text = "Geri",
                    onClick = onBack,
                    enabled = !loading,
                    modifier = Modifier.weight(1f)
                )
                AtuPrimaryButton(
                    text = if (currentStepIndex == steps.lastIndex) "Yoxla" else "İndi çək",
                    onClick = { if (!loading) captureCurrentStep() },
                    enabled = !loading && !analyzerAvailable,
                    loading = loading,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(6.dp))
        }

        FaceVerificationFeedbackOverlay(
            state = feedbackState,
            message = message
        )
    }
}

@Composable
private fun FaceVerificationFeedbackOverlay(
    state: FaceVerificationFeedbackState,
    message: String
) {
    val visible = state != FaceVerificationFeedbackState.Idle
    val success = state == FaceVerificationFeedbackState.Success
    val iconScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.92f,
        animationSpec = tween(320),
        label = "feedbackScale"
    )

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(220)) + scaleIn(tween(320), initialScale = 0.92f),
        exit = fadeOut(tween(180)) + scaleOut(tween(180), targetScale = 0.96f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.54f)),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(30.dp),
                color = AtuWhite,
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .graphicsLayer {
                        scaleX = iconScale
                        scaleY = iconScale
                    }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (success) Color(0xFFEAF8EF) else Color(0xFFFFF1F1),
                        modifier = Modifier.size(76.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (success) Icons.Outlined.Shield else Icons.Outlined.WarningAmber,
                                contentDescription = null,
                                tint = if (success) Color(0xFF16A34A) else AtuDanger,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                    }
                    Text(
                        text = if (success) "Doğrulama uğurludur" else "Doğrulama alınmadı",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (message.isBlank()) {
                            if (success) "Şəxsiyyət təsdiqləndi, hesab açılır..." else "Üz və canlılıq yoxlaması uğursuz oldu."
                        } else {
                            message
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
