package com.atu.campus.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.atu.campus.R
import com.atu.campus.ui.theme.AtuMagenta
import com.atu.campus.ui.theme.AtuPrimary
import com.atu.campus.ui.theme.AtuWarning
import com.atu.campus.ui.theme.AtuWhite
import com.atu.campus.ui.theme.AtuWine
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    var entered by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        entered = true
        delay(2750)
        onFinished()
    }

    val ambient = rememberInfiniteTransition(label = "atuSplash")
    val backgroundShift by ambient.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "backgroundShift"
    )
    val particleDrift by ambient.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleDrift"
    )
    val loadingShift by ambient.animateFloat(
        initialValue = -0.3f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "loadingShift"
    )
    val logoBreath by ambient.animateFloat(
        initialValue = 0.985f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoBreath"
    )
    val glowPulse by ambient.animateFloat(
        initialValue = 0.18f,
        targetValue = 0.38f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    val sceneAlpha by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(720, easing = FastOutSlowInEasing),
        label = "sceneAlpha"
    )
    val logoScale by animateFloatAsState(
        targetValue = if (entered) 1f else 0.8f,
        animationSpec = tween(1150, easing = FastOutSlowInEasing),
        label = "logoScale"
    )
    val logoOffset by animateFloatAsState(
        targetValue = if (entered) 0f else 30f,
        animationSpec = tween(980, easing = FastOutSlowInEasing),
        label = "logoOffset"
    )
    val textOffset by animateFloatAsState(
        targetValue = if (entered) 0f else 26f,
        animationSpec = tween(1080, delayMillis = 120, easing = FastOutSlowInEasing),
        label = "textOffset"
    )
    val textAlpha by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(1000, delayMillis = 140, easing = FastOutSlowInEasing),
        label = "textAlpha"
    )
    val beamAlpha by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(680, delayMillis = 120, easing = FastOutSlowInEasing),
        label = "beamAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF4F0623),
                        AtuPrimary,
                        Color(0xFF970A47),
                        Color(0xFF7A003C)
                    )
                )
            )
    ) {
        SplashAtmosphere(
            backgroundShift = backgroundShift,
            particleDrift = particleDrift,
            alpha = sceneAlpha
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
                .graphicsLayer { alpha = sceneAlpha },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .graphicsLayer { alpha = beamAlpha }
                ) {
                    drawRoundRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                AtuWarning.copy(alpha = 0.32f),
                                Color.White.copy(alpha = 0.12f),
                                Color.Transparent
                            ),
                            center = Offset(size.width / 2f, size.height / 2f),
                            radius = size.minDimension * 0.68f
                        ),
                        topLeft = Offset(size.width * 0.14f, size.height * 0.1f),
                        size = Size(size.width * 0.72f, size.height * 0.8f),
                        cornerRadius = CornerRadius(size.height * 0.4f, size.height * 0.4f),
                        blendMode = BlendMode.Screen
                    )
                }

                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .graphicsLayer {
                            scaleX = logoBreath
                            scaleY = logoBreath
                        }
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = glowPulse))
                )

                Surface(
                    modifier = Modifier
                        .size(102.dp)
                        .graphicsLayer {
                            scaleX = logoScale
                            scaleY = logoScale
                            translationY = logoOffset
                        },
                    shape = RoundedCornerShape(30.dp),
                    color = Color.White.copy(alpha = 0.98f),
                    shadowElevation = 26.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.atu_logo),
                            contentDescription = "ATU",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Column(
                modifier = Modifier.graphicsLayer {
                    alpha = textAlpha
                    translationY = textOffset
                },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "ATU Campus",
                    color = AtuWhite,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Az\u0259rbaycan Texnologiya Universitetinin R\u0259q\u0259msal Platformas\u0131",
                    color = Color.White.copy(alpha = 0.84f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "T\u0259hsil \u2022 Kampus \u2022 AI \u2022 Karyera",
                    color = Color(0xFFF7D88A),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(34.dp))

            SplashLoadingLine(progressShift = loadingShift, alpha = textAlpha)

            Spacer(Modifier.height(22.dp))

            Surface(
                color = Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
            ) {
                Text(
                    text = "Official ATU Digital Ecosystem",
                    color = Color.White.copy(alpha = 0.92f),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun SplashAtmosphere(
    backgroundShift: Float,
    particleDrift: Float,
    alpha: Float
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { this.alpha = alpha }
    ) {
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color.White.copy(alpha = 0.08f), Color.Transparent),
                center = Offset(size.width * 0.18f, size.height * 0.14f),
                radius = size.width * 0.78f
            ),
            radius = size.width * 0.66f,
            center = Offset(size.width * 0.18f, size.height * 0.14f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                listOf(AtuMagenta.copy(alpha = 0.12f), Color.Transparent),
                center = Offset(size.width * 0.92f, size.height * 0.82f),
                radius = size.width * 0.66f
            ),
            radius = size.width * 0.58f,
            center = Offset(size.width * 0.92f, size.height * 0.82f)
        )
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.06f),
                    Color.Transparent
                ),
                start = Offset(size.width * backgroundShift, 0f),
                end = Offset(size.width * (backgroundShift - 0.2f), size.height)
            ),
            topLeft = Offset(size.width * 0.78f, -size.height * 0.1f),
            size = Size(size.width * 0.18f, size.height * 1.2f),
            cornerRadius = CornerRadius(999f, 999f),
            blendMode = BlendMode.Screen
        )

        val stroke = Stroke(
            width = 1.4.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10.dp.toPx(), 12.dp.toPx()))
        )
        drawLine(
            color = Color.White.copy(alpha = 0.08f),
            start = Offset(size.width * 0.18f, size.height * 0.28f),
            end = Offset(size.width * 0.82f, size.height * 0.34f),
            strokeWidth = 1.2.dp.toPx()
        )
        drawLine(
            color = Color.White.copy(alpha = 0.06f),
            start = Offset(size.width * 0.22f, size.height * 0.62f),
            end = Offset(size.width * 0.75f, size.height * 0.55f),
            strokeWidth = 1.1.dp.toPx()
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.09f),
            radius = 4.dp.toPx(),
            center = Offset(size.width * 0.26f, size.height * (0.28f + particleDrift * 0.05f))
        )
        drawCircle(
            color = Color(0xFFF7D88A).copy(alpha = 0.12f),
            radius = 3.5.dp.toPx(),
            center = Offset(size.width * 0.61f, size.height * (0.35f - particleDrift * 0.04f))
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.08f),
            radius = 4.6.dp.toPx(),
            center = Offset(size.width * 0.72f, size.height * (0.56f + particleDrift * 0.03f))
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.06f),
            radius = size.width * 0.46f,
            center = Offset(size.width * 0.08f, size.height * 0.14f),
            style = stroke
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.05f),
            radius = size.width * 0.42f,
            center = Offset(size.width * 1.02f, size.height * 0.84f),
            style = stroke
        )
    }
}

@Composable
private fun SplashLoadingLine(
    progressShift: Float,
    alpha: Float
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.44f)
            .height(10.dp)
            .clip(RoundedCornerShape(99.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .graphicsLayer { this.alpha = alpha }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.06f),
                        Color.White.copy(alpha = 0.12f),
                        Color.White.copy(alpha = 0.06f)
                    )
                ),
                cornerRadius = CornerRadius(size.height / 2f, size.height / 2f)
            )
            val lineWidth = size.width * 0.34f
            drawRoundRect(
                color = Color.White.copy(alpha = 0.06f),
                topLeft = Offset(0f, size.height * 0.26f),
                size = Size(size.width, size.height * 0.48f),
                cornerRadius = CornerRadius(size.height / 2f, size.height / 2f)
            )
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.45f),
                        Color(0xFFF7D88A).copy(alpha = 0.9f),
                        Color.White.copy(alpha = 0.95f),
                        Color(0xFFF7D88A).copy(alpha = 0.72f),
                        Color.Transparent
                    )
                ),
                topLeft = Offset(size.width * progressShift - lineWidth, size.height * 0.08f),
                size = Size(lineWidth, size.height * 0.84f),
                cornerRadius = CornerRadius(size.height / 2f, size.height / 2f)
            )
            drawLine(
                brush = Brush.horizontalGradient(
                    listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.55f),
                        Color.Transparent
                    )
                ),
                start = Offset(size.width * progressShift - lineWidth * 0.08f, size.height / 2f),
                end = Offset(size.width * progressShift + lineWidth * 0.18f, size.height / 2f),
                strokeWidth = size.height * 0.22f,
                cap = StrokeCap.Round
            )
        }
    }
}
