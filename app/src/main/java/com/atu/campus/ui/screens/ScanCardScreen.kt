package com.atu.campus.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.outlined.Camera
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.atu.campus.services.CameraImageStore
import com.atu.campus.ui.components.AtuStatusBadge
import com.atu.campus.ui.components.atuPressScale
import com.atu.campus.ui.theme.AtuDanger
import com.atu.campus.ui.theme.AtuPrimary
import com.atu.campus.ui.theme.AtuTint
import com.atu.campus.ui.theme.AtuWhite
import java.util.concurrent.TimeUnit

@Composable
fun ScanCardScreen(
    imageStore: CameraImageStore,
    onImageCaptured: (imagePath: String) -> Unit,
    onPermissionMissing: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mainExecutor = remember { ContextCompat.getMainExecutor(context) }
    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var cameraError by remember { mutableStateOf<String?>(null) }
    var isCapturing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasPermission) onPermissionMissing()
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
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                    .build()
                cameraProvider.unbindAll()
                val camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    capture
                )
                imageCapture = capture
                cameraError = null

                val point = previewView.meteringPointFactory.createPoint(0.5f, 0.5f)
                camera.cameraControl.startFocusAndMetering(
                    FocusMeteringAction.Builder(point)
                        .setAutoCancelDuration(3, TimeUnit.SECONDS)
                        .build()
                )
            } catch (_: Exception) {
                cameraError = "Kamera haz\u0131r deyil. Cihazda kameran\u0131 yoxlay\u0131n."
            }
        }
        providerFuture.addListener(listener, mainExecutor)
        onDispose {
            try {
                providerFuture.get().unbindAll()
            } catch (_: Exception) {
            }
        }
    }

    fun captureCardNumber() {
        val capture = imageCapture ?: run {
            cameraError = "Kamera h\u0259l\u0259 haz\u0131r deyil."
            return
        }
        isCapturing = true
        val file = imageStore.createCardImageFile()
        capture.takePicture(
            ImageCapture.OutputFileOptions.Builder(file).build(),
            mainExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    isCapturing = false
                    onImageCaptured(file.absolutePath)
                }

                override fun onError(exception: ImageCaptureException) {
                    isCapturing = false
                    cameraError = "\u015E\u0259kil \u00E7\u0259kil\u0259 bilm\u0259di. Yenid\u0259n c\u0259hd edin."
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
        CardNumberOverlay()

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.48f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, AtuWhite.copy(alpha = 0.14f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Lock, contentDescription = null, tint = AtuWhite)
                    Text(
                        "Yaln\u0131z v\u0259siq\u0259 n\u00F6mr\u0259si oxunur",
                        color = AtuWhite,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            FloatingActionButton(
                onClick = { if (!isCapturing) captureCardNumber() },
                containerColor = AtuWhite,
                contentColor = AtuPrimary,
                shape = CircleShape,
                modifier = Modifier.size(84.dp).atuPressScale()
            ) {
                Icon(
                    Icons.Outlined.Camera,
                    contentDescription = "V\u0259siq\u0259 n\u00F6mr\u0259sini \u00E7\u0259k",
                    modifier = Modifier.size(35.dp)
                )
            }

            Text(
                text = if (isCapturing) "\u015E\u0259kil haz\u0131rlan\u0131r..." else "Oxutmaq \u00FC\u00E7\u00FCn d\u00FCym\u0259y\u0259 toxunun",
                color = AtuWhite,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            if (cameraError != null) {
                Text(
                    text = cameraError.orEmpty(),
                    color = AtuDanger,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .background(AtuTint, RoundedCornerShape(14.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun CardNumberOverlay() {
    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val frameWidth = size.width * 0.86f
            val frameHeight = 104.dp.toPx()
            val left = (size.width - frameWidth) / 2f
            val top = size.height * 0.38f
            val corner = 22.dp.toPx()

            drawRect(Color.Black.copy(alpha = 0.48f))
            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(left, top),
                size = Size(frameWidth, frameHeight),
                cornerRadius = CornerRadius(corner),
                blendMode = BlendMode.Clear
            )
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    listOf(
                        AtuPrimary.copy(alpha = 0.55f),
                        AtuWhite,
                        AtuPrimary.copy(alpha = 0.55f)
                    )
                ),
                topLeft = Offset(left, top),
                size = Size(frameWidth, frameHeight),
                cornerRadius = CornerRadius(corner),
                style = Stroke(width = 2.5.dp.toPx())
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 28.dp, start = 22.dp, end = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AtuStatusBadge(
                text = "Bir add\u0131m",
                darkMode = true,
                icon = Icons.Outlined.CreditCard
            )
            Text(
                "T\u0259l\u0259b\u0259 v\u0259siq\u0259si n\u00F6mr\u0259sini \u00E7\u0259r\u00E7iv\u0259y\u0259 yerl\u0259\u015Fdirin",
                color = AtuWhite,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Text(
                "N\u00F6mr\u0259 ayd\u0131n, d\u00FCz v\u0259 i\u015F\u0131ql\u0131 g\u00F6r\u00FCnm\u0259lidir",
                color = AtuWhite.copy(alpha = 0.78f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}
