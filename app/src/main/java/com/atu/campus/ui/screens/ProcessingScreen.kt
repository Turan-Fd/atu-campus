package com.atu.campus.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.atu.campus.services.BackendStudentService
import com.atu.campus.services.OcrService
import com.atu.campus.services.StudentLookupResult
import com.atu.campus.ui.components.AtuHeroCard
import com.atu.campus.ui.components.AtuInlineNote
import com.atu.campus.ui.components.AtuPremiumCard
import com.atu.campus.ui.components.AtuScreen
import com.atu.campus.ui.components.AtuStatusBadge
import com.atu.campus.ui.components.atuPalette
import com.atu.campus.ui.theme.AtuMagenta
import com.atu.campus.ui.theme.AtuPrimary
import com.atu.campus.ui.theme.AtuSuccess
import com.atu.campus.ui.theme.AtuWhite
import kotlinx.coroutines.delay

@Composable
fun ProcessingScreen(
    ocrService: OcrService,
    backendStudentService: BackendStudentService,
    imagePath: String?,
    onProcessed: (StudentLookupResult) -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }
    val steps = listOf("V\u0259siq\u0259 n\u00F6mr\u0259si oxunur", "N\u00F6mr\u0259 d\u0259qiql\u0259\u015Fdirilir", "T\u0259l\u0259b\u0259 profili tap\u0131l\u0131r")

    LaunchedEffect(imagePath) {
        currentStep = 0
        delay(450)
        val scan = ocrService.readCardNumber(imagePath)
        currentStep = 1
        delay(520)
        currentStep = 2
        val verified = backendStudentService.lookupByCardScan(scan)
        delay(450)
        onProcessed(verified)
    }

    val transition = rememberInfiniteTransition(label = "processing")
    val scan by transition.animateFloat(0f, 1f, infiniteRepeatable(tween(1400), RepeatMode.Restart), label = "scan")
    val pulse by transition.animateFloat(0.35f, 1f, infiniteRepeatable(tween(900), RepeatMode.Reverse), label = "pulse")

    AtuScreen(verticalArrangement = Arrangement.spacedBy(22.dp)) {
        Spacer(Modifier.height(20.dp))
        AtuStatusBadge(text = "Lokal emal", icon = Icons.Outlined.Security)
        AtuHeroCard(
            title = steps[currentStep],
            subtitle = "V\u0259siq\u0259 n\u00F6mr\u0259si lokal OCR il\u0259 oxunur v\u0259 t\u0259l\u0259b\u0259 datas\u0131nda tam uy\u011Funluqla yoxlan\u0131l\u0131r.",
            minHeight = 150.dp
        ) {
            Icon(Icons.Outlined.DocumentScanner, contentDescription = null, tint = AtuWhite.copy(alpha = 0.28f), modifier = Modifier.align(Alignment.TopEnd).padding(6.dp))
        }

        AtuPremiumCard(radius = 28.dp) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(214.dp)
                    .background(Brush.linearGradient(listOf(AtuPrimary, AtuMagenta)), RoundedCornerShape(26.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cardHeight = size.height * 0.62f
                    val cardWidth = size.width * 0.9f
                    val left = (size.width - cardWidth) / 2f
                    val top = (size.height - cardHeight) / 2f
                    drawRoundRect(
                        color = AtuWhite.copy(alpha = 0.13f),
                        topLeft = Offset(left, top),
                        size = Size(cardWidth, cardHeight),
                        cornerRadius = CornerRadius(28.dp.toPx())
                    )
                    drawRoundRect(
                        color = AtuWhite.copy(alpha = 0.22f + pulse * 0.18f),
                        topLeft = Offset(left, top),
                        size = Size(cardWidth, cardHeight),
                        cornerRadius = CornerRadius(28.dp.toPx()),
                        style = Stroke(width = 2.dp.toPx())
                    )
                    drawLine(
                        brush = Brush.horizontalGradient(listOf(AtuWhite.copy(alpha = 0f), AtuWhite, AtuWhite.copy(alpha = 0f))),
                        start = Offset(left + 18.dp.toPx(), top + cardHeight * scan),
                        end = Offset(left + cardWidth - 18.dp.toPx(), top + cardHeight * scan),
                        strokeWidth = 4.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
        }

        AtuPremiumCard {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                steps.forEachIndexed { index, label ->
                    StepRow(label, active = index == currentStep, done = index < currentStep)
                }
            }
        }

        AtuInlineNote(text = "Kart \u015F\u0259kli daimi saxlanm\u0131r. Profil yaln\u0131z v\u0259siq\u0259 n\u00F6mr\u0259si real t\u0259l\u0259b\u0259 datas\u0131nda unikal tap\u0131ld\u0131qda a\u00E7\u0131l\u0131r.")
    }
}

@Composable
private fun StepRow(label: String, active: Boolean, done: Boolean) {
    val color = when {
        done -> AtuSuccess
        active -> AtuPrimary
        else -> atuPalette(false).muted
    }
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(30.dp).clip(CircleShape).background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Crossfade(targetState = done, label = "stepDone") { isDone ->
                if (isDone) {
                    Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                } else {
                    Box(Modifier.size(if (active) 11.dp else 7.dp).clip(CircleShape).background(color))
                }
            }
        }
        Text(label, color = color, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
    }
}
