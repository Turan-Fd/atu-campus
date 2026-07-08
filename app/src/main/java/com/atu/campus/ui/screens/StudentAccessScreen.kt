package com.atu.campus.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.atu.campus.R
import com.atu.campus.ui.components.AtuInlineNote
import com.atu.campus.ui.components.AtuInputField
import com.atu.campus.ui.components.AtuLogoHeader
import com.atu.campus.ui.components.AtuPrimaryButton
import com.atu.campus.ui.components.AtuScreen
import com.atu.campus.ui.components.AtuSoftCard
import com.atu.campus.ui.theme.AtuColors

@Composable
fun StudentAccessScreen(
    message: String,
    loading: Boolean,
    onContinue: (String) -> Unit
) {
    var code by remember { mutableStateOf("") }
    var showGuide by remember { mutableStateOf(false) }

    AtuScreen(verticalArrangement = Arrangement.spacedBy(18.dp)) { palette ->
        AtuLogoHeader(modifier = Modifier.padding(top = 8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(182.dp)
                .clip(RoundedCornerShape(30.dp))
        ) {
            Image(
                painter = painterResource(R.drawable.atu_logo),
                contentDescription = "ATU",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 26.dp)
                    .size(112.dp),
                alpha = 0.92f
            )
            Surface(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(30.dp)),
                color = AtuColors.Surface.copy(alpha = 0.42f)
            ) {}
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Xoş gəlmisiniz!",
                    color = palette.text,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Hesabınıza daxil olaraq universitet həyatınızı asanlaşdırın.",
                    color = palette.textSecondary,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        AtuSoftCard(radius = 26.dp) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tələbə məlumatı",
                    color = palette.text,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = { showGuide = true }) {
                    Icon(Icons.Outlined.Info, contentDescription = "Təlimat", tint = palette.primary)
                }
            }

            AtuInputField(
                value = code,
                onValueChange = { code = it.filter(Char::isDigit).take(6) },
                label = "Tələbə vəsiqəsi nömrəsi",
                placeholder = "Məs: 193253",
                leadingIcon = Icons.Outlined.Badge
            )

            AtuInlineNote(
                icon = Icons.Outlined.Lock,
                text = "Bu girişdə parol tələb olunmur. Növbəti addımda üz doğrulaması ilə davam edəcəksiniz."
            )

            AtuPrimaryButton(
                text = "Daxil ol",
                enabled = code.length == 6,
                loading = loading,
                showArrow = true,
                onClick = { onContinue(code) }
            )

            Text(
                text = "və ya",
                color = palette.textMuted,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = palette.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, palette.border)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Fingerprint, contentDescription = null, tint = palette.primary)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Biometrik giriş",
                            color = palette.text,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "Üz ilə təhlükəsiz təsdiq",
                            color = palette.textSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        AnimatedContent(targetState = message, label = "accessMessage") { current ->
            AtuInlineNote(
                icon = Icons.Outlined.Lock,
                text = if (current.isNotBlank()) {
                    current
                } else {
                    "Mövcud tələbə məlumatı saxlanılır və növbəti addımda üz doğrulaması ilə yoxlanılır."
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Probleminiz var? Dəstək mərkəzi ilə əlaqə saxlayın",
            color = palette.textMuted,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (showGuide) {
        Dialog(onDismissRequest = { showGuide = false }) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Təlimat", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                        IconButton(onClick = { showGuide = false }) {
                            Icon(Icons.Outlined.Close, contentDescription = "Bağla")
                        }
                    }
                    Image(
                        painter = painterResource(R.drawable.student_card_guide),
                        contentDescription = "Tələbə vəsiqəsi nümunəsi",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp)),
                        contentScale = ContentScale.FillWidth
                    )
                    AtuInlineNote(
                        icon = Icons.Outlined.Info,
                        text = "Nömrəni vəsiqənin ön hissəsindəki “Tələbə vəsiqəsi №” sahəsindən daxil edin."
                    )
                }
            }
        }
    }
}
