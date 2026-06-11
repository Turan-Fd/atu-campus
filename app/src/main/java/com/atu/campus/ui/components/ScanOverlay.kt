package com.atu.campus.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.atu.campus.ui.theme.AtuPrimary
import com.atu.campus.ui.theme.AtuWhite

@Composable
fun ScanOverlay(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "scan")
    val pulse by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanPulse"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
        ) {
            val frameWidth = size.width
            val frameHeight = frameWidth * 0.62f
            val left = 0f
            val top = (size.height - frameHeight) / 2f
            val corner = 28.dp.toPx()

            drawRoundRect(
                brush = Brush.linearGradient(listOf(AtuWhite, AtuPrimary)),
                topLeft = Offset(left, top),
                size = Size(frameWidth, frameHeight),
                cornerRadius = CornerRadius(corner, corner),
                style = Stroke(width = 3.dp.toPx()),
                alpha = pulse
            )
            drawLine(
                brush = Brush.horizontalGradient(listOf(Color.Transparent, AtuWhite, Color.Transparent)),
                start = Offset(left + 18.dp.toPx(), top + frameHeight * pulse),
                end = Offset(left + frameWidth - 18.dp.toPx(), top + frameHeight * pulse),
                strokeWidth = 2.dp.toPx(),
                alpha = 0.95f
            )
        }
    }
}
