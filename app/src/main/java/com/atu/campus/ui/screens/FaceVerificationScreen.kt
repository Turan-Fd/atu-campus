package com.atu.campus.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.graphics.Bitmap
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
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.material.icons.outlined.Camera
import androidx.compose.material.icons.outlined.Cameraswitch
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FaceRetouchingNatural
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.atu.campus.ui.components.AtuScanOverlay
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

@Composable
fun FaceVerificationScreen(
    studentPreviewName: String,
    studentPreviewGroup: String,
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
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }
    val challengeAnalyzer = remember { FaceChallengeAnalyzer(context.applicationContext) }

    val steps = remember {
        listOf(
            FaceChallengeStep("1/3", "Kameraya düz baxın və ilk kadrı çəkin"),
            FaceChallengeStep("2/3", "Göz qırpın və ikinci kadrı çəkin"),
            FaceChallengeStep("3/3", "Başınızı sola çevirin və üçüncü kadrı çəkin")
        )
    }
    val capturedPaths = remember { mutableStateListOf<String>() }
    var currentStepIndex by remember { mutableIntStateOf(0) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var localError by remember { mutableStateOf("") }
    var permissionMissing by remember { mutableStateOf(false) }
    var facePresent by remember { mutableStateOf(false) }
    var blinkDetected by remember { mutableStateOf(false) }
    var headTurnLeftDetected by remember { mutableStateOf(false) }
    var analyzerAvailable by remember { mutableStateOf(challengeAnalyzer.isAvailable()) }
    var analyzerConfidence by remember { mutableFloatStateOf(0f) }
    var autoCaptureInFlight by remember { mutableStateOf(false) }
    val pulseTransition = rememberInfiniteTransition(label = "capturePulse")
    val pulseScale by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            permissionMissing = true
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    DisposableEffect(lifecycleOwner, previewView) {
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
                localError = ""
            } catch (_: Exception) {
                localError = "Ön kamera açılmadı. Cihaz icazələrini yoxlayın."
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

    fun captureCurrentStep() {
        if (autoCaptureInFlight) return
        val capture = imageCapture ?: run {
            localError = "Kamera hazır deyil."
            return
        }
        if (currentStepIndex >= 1 && !facePresent) {
            localError = "Üz tam görünməlidir."
            return
        }
        if (currentStepIndex == 1 && analyzerAvailable && !blinkDetected) {
            localError = "Əvvəl göz qırpın, sonra kadr çəkin."
            return
        }
        if (currentStepIndex == 2 && analyzerAvailable && !headTurnLeftDetected) {
            localError = "Əvvəl başınızı sola çevirin, sonra kadr çəkin."
            return
        }
        val file = imageStore.createCardImageFile()
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

                    if (currentStepIndex < steps.lastIndex) {
                        currentStepIndex += 1
                    } else {
                        onSubmit(
                            capturedPaths.toList(),
                            blinkDetected,
                            headTurnLeftDetected
                        )
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
            autoCaptureInFlight = true
            delay(650)
            captureCurrentStep()
        }
    }

    val step = steps[currentStepIndex.coerceIn(0, steps.lastIndex)]

    Box(modifier = Modifier.fillMaxSize().background(androidx.compose.ui.graphics.Color.Black)) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
        AtuScanOverlay(
            progressText = step.title,
            instruction = step.instruction
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.42f),
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (studentPreviewName.isBlank()) "Tələbə" else studentPreviewName,
                        color = AtuWhite,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = if (studentPreviewGroup.isBlank()) "Üz doğrulaması" else "$studentPreviewGroup qrupu",
                        color = AtuWhite.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        repeat(steps.size) { index ->
                            AtuStatusBadge(
                                text = "${index + 1}",
                                darkMode = true,
                                icon = if (index < capturedPaths.size) Icons.Outlined.CheckCircle else Icons.Outlined.FaceRetouchingNatural,
                                success = index < capturedPaths.size
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AtuStatusBadge(
                            text = if (facePresent) "Üz göründü" else "Üz gözlənilir",
                            darkMode = true,
                            icon = Icons.Outlined.FaceRetouchingNatural,
                            success = facePresent
                        )
                        AtuStatusBadge(
                            text = if (blinkDetected) "Blink ok" else "Blink",
                            darkMode = true,
                            icon = Icons.Outlined.CheckCircle,
                            success = blinkDetected
                        )
                        AtuStatusBadge(
                            text = if (headTurnLeftDetected) "Sola baxış ok" else "Sola baxın",
                            darkMode = true,
                            icon = Icons.Outlined.Cameraswitch,
                            success = headTurnLeftDetected
                        )
                    }
                }
            }

            FloatingActionButton(
                onClick = { if (!loading) captureCurrentStep() },
                containerColor = AtuWhite,
                contentColor = AtuPrimary,
                shape = CircleShape,
                modifier = Modifier
                    .size(84.dp)
                    .graphicsLayer {
                        val scale = if (autoCaptureInFlight || analyzerAvailable) pulseScale else 1f
                        scaleX = scale
                        scaleY = scale
                    }
                    .atuPressScale()
            ) {
                Icon(Icons.Outlined.Camera, contentDescription = "Kadr çək", modifier = Modifier.size(34.dp))
            }

            Text(
                text = when {
                    loading -> "Üz doğrulanır..."
                    analyzerAvailable -> "Uyğun hərəkət aşkar ediləndə kadr avtomatik çəkiləcək"
                    else -> "Cari addımı tamamlamaq üçün düyməyə toxunun"
                },
                color = AtuWhite,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            if (analyzerAvailable) {
                Text(
                    text = "Canlı analiz aktivdir • etibar ${(analyzerConfidence * 100).toInt()}%",
                    color = AtuWhite.copy(alpha = 0.82f),
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
                        .padding(horizontal = 12.dp, vertical = 10.dp)
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
                    text = if (currentStepIndex == steps.lastIndex) "Yoxla" else "Növbəti kadr",
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
                .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.54f)),
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
                        color = if (success) androidx.compose.ui.graphics.Color(0xFFEAF8EF) else androidx.compose.ui.graphics.Color(0xFFFFF1F1),
                        modifier = Modifier.size(76.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (success) Icons.Outlined.Shield else Icons.Outlined.WarningAmber,
                                contentDescription = null,
                                tint = if (success) androidx.compose.ui.graphics.Color(0xFF16A34A) else AtuDanger,
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
