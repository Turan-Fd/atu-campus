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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.atu.campus.R
import com.atu.campus.ui.theme.AtuColors
import com.atu.campus.ui.theme.AtuPrimary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
        delay(1900)
        onFinished()
    }

    val ambient = rememberInfiniteTransition(label = "atuSplashAmbient")
    val loaderShift by ambient.animateFloat(
        initialValue = -0.2f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1450, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "loaderShift"
    )
    val orbShift by ambient.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orbShift"
    )

    val contentAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(650, easing = FastOutSlowInEasing),
        label = "contentAlpha"
    )
    val contentOffset by animateFloatAsState(
        targetValue = if (visible) 0f else 24f,
        animationSpec = tween(820, easing = FastOutSlowInEasing),
        label = "contentOffset"
    )
    val logoScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.92f,
        animationSpec = tween(900, easing = FastOutSlowInEasing),
        label = "logoScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8F3F6),
                        Color(0xFFF7F8FC),
                        Color(0xFFF3F1F8)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(AtuPrimary.copy(alpha = 0.08f), Color.Transparent),
                    center = Offset(size.width * (0.18f + (orbShift * 0.06f)), size.height * 0.16f),
                    radius = size.width * 0.52f
                ),
                radius = size.width * 0.46f,
                center = Offset(size.width * (0.18f + (orbShift * 0.06f)), size.height * 0.16f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(AtuColors.SoftPurple.copy(alpha = 0.22f), Color.Transparent),
                    center = Offset(size.width * 0.86f, size.height * (0.82f - (orbShift * 0.04f))),
                    radius = size.width * 0.44f
                ),
                radius = size.width * 0.4f,
                center = Offset(size.width * 0.86f, size.height * (0.82f - (orbShift * 0.04f)))
            )
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.72f),
                        Color.Transparent
                    )
                ),
                topLeft = Offset(size.width * 0.62f, size.height * 0.08f),
                size = Size(size.width * 0.22f, size.height * 0.44f),
                cornerRadius = CornerRadius(999f, 999f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 36.dp)
                .graphicsLayer {
                    alpha = contentAlpha
                    translationY = contentOffset
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier
                    .size(132.dp)
                    .graphicsLayer {
                        scaleX = logoScale
                        scaleY = logoScale
                    },
                shape = RoundedCornerShape(36.dp),
                color = Color.White.copy(alpha = 0.96f),
                shadowElevation = 18.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.atu_logo),
                        contentDescription = "ATU"
                    )
                }
            }

            Spacer(Modifier.height(26.dp))

            Text(
                text = "ATU Campus",
                color = Color(0xFF181B24),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Azərbaycan Texnologiya Universitetinin rəqəmsal tələbə platforması",
                color = Color(0xFF6B7280),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            Surface(
                shape = RoundedCornerShape(999.dp),
                color = Color.White.copy(alpha = 0.9f),
                shadowElevation = 10.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.42f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color(0xFFF0E8ED))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val glowWidth = size.width * 0.34f
                        drawRoundRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    AtuPrimary.copy(alpha = 0.42f),
                                    Color(0xFFD9A9BA),
                                    AtuPrimary.copy(alpha = 0.94f),
                                    Color.Transparent
                                )
                            ),
                            topLeft = Offset(size.width * loaderShift - glowWidth, 0f),
                            size = Size(glowWidth, size.height),
                            cornerRadius = CornerRadius(size.height / 2f, size.height / 2f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            Text(
                text = "Yüklənir",
                color = Color(0xFF8C5870),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
