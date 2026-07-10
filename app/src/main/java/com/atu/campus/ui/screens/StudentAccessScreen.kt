package com.atu.campus.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Close
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.atu.campus.R
import com.atu.campus.ui.components.AtuInlineNote
import com.atu.campus.ui.components.AtuInputField
import com.atu.campus.ui.components.AtuPrimaryButton
import com.atu.campus.ui.components.AtuScreen
import com.atu.campus.ui.components.AtuSoftCard
import com.atu.campus.ui.theme.AtuColors
import com.atu.campus.ui.theme.AtuPrimary
import com.atu.campus.ui.theme.AtuWhite

private const val NEWS_ADMIN_ACCESS_CODE = "1970103"
private const val SMS_ADMIN_ACCESS_CODE = "899913"

@Composable
fun StudentAccessScreen(
    message: String,
    loading: Boolean,
    onContinue: (studentId: String, fin: String) -> Unit
) {
    var studentId by remember { mutableStateOf("") }
    var fin by remember { mutableStateOf("") }
    var showGuide by remember { mutableStateOf(false) }

    val normalizedStudentId = studentId.filter(Char::isDigit)
    val normalizedFin = fin.trim().uppercase()
    val adminMode = normalizedStudentId == NEWS_ADMIN_ACCESS_CODE || normalizedStudentId == SMS_ADMIN_ACCESS_CODE
    val canContinue = if (adminMode) {
        normalizedStudentId.length >= 6
    } else {
        normalizedStudentId.length >= 6 && normalizedFin.length >= 7
    }

    AtuScreen(verticalArrangement = Arrangement.spacedBy(18.dp)) { palette ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(34.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            AtuWhite,
                            AtuColors.SoftPrimary,
                            AtuColors.SoftPurple
                        )
                    )
                )
                .padding(horizontal = 22.dp, vertical = 24.dp)
        ) {
            val compact = maxWidth < 360.dp
            val heroHeight = if (compact) 208.dp else 236.dp
            val logoSize = if (compact) 98.dp else 124.dp

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(heroHeight)
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .fillMaxWidth(if (compact) 0.78f else 0.72f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Xoş gəlmisiniz",
                        color = palette.text,
                        style = if (compact) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Tələbə nömrənizi və FIN kodunuzu daxil edin. Uyğunluq təsdiqləndikdən sonra üz doğrulaması ilə davam edəcəksiniz.",
                        color = palette.textSecondary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Image(
                    painter = painterResource(R.drawable.atu_logo),
                    contentDescription = "ATU",
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(logoSize),
                    alpha = 0.94f
                )
            }
        }

        AtuSoftCard(radius = 28.dp) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Giriş məlumatları",
                    color = palette.text,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = { showGuide = true }) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Təlimat",
                        tint = palette.primary
                    )
                }
            }

            AtuInputField(
                value = studentId,
                onValueChange = { studentId = it.filter(Char::isDigit).take(8) },
                label = "Tələbə vəsiqəsi nömrəsi",
                placeholder = "Məs: 193253",
                leadingIcon = Icons.Outlined.Badge,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            if (!adminMode) {
                AtuInputField(
                    value = fin,
                    onValueChange = { value ->
                        fin = value
                            .uppercase()
                            .filter { char -> char.isLetterOrDigit() }
                            .take(7)
                    },
                    label = "FIN kodu",
                    placeholder = "Məs: 7BV5777",
                    leadingIcon = Icons.Outlined.Lock,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters,
                        keyboardType = KeyboardType.Ascii
                    )
                )
            }

            if (adminMode) {
                AtuInlineNote(
                    icon = Icons.Outlined.Lock,
                    text = "Administrator girişi aşkarlandı. Növbəti addımda parol yoxlaması açılacaq."
                )
            }

            AtuPrimaryButton(
                text = if (adminMode) "Davam et" else "Üz doğrulamasına keç",
                enabled = canContinue,
                loading = loading,
                showArrow = true,
                onClick = { onContinue(normalizedStudentId, normalizedFin) }
            )

            Text(
                text = "və ya",
                color = palette.textMuted,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Surface(
                shape = RoundedCornerShape(20.dp),
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
                            .size(44.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(AtuColors.SoftPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Lock, contentDescription = null, tint = AtuPrimary)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Biometrik giriş",
                            color = palette.text,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Əvvəl məlumat uyğunluğu, sonra canlı üz təsdiqi",
                            color = palette.textSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        AnimatedContent(targetState = message, label = "accessMessage") { current ->
            if (current.isNotBlank()) {
                AtuInlineNote(
                    icon = Icons.Outlined.Lock,
                    text = current
                )
            } else {
                Spacer(modifier = Modifier.height(0.dp))
            }
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
                        Text(
                            text = "Təlimat",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
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
                        text = "Nömrəni vəsiqənin ön hissəsindəki \"Tələbə vəsiqəsi №\" sahəsindən, FIN kodunu isə şəxsi məlumatlarınızdan daxil edin."
                    )
                }
            }
        }
    }
}
