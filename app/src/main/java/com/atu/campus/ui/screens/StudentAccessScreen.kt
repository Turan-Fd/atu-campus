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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.atu.campus.R
import com.atu.campus.ui.components.AtuHeroCard
import com.atu.campus.ui.components.AtuInlineNote
import com.atu.campus.ui.components.AtuPrimaryButton
import com.atu.campus.ui.components.AtuScreen
import com.atu.campus.ui.components.PremiumCard
import com.atu.campus.ui.theme.AtuPrimary

@Composable
fun StudentAccessScreen(
    message: String,
    loading: Boolean,
    onContinue: (String) -> Unit
) {
    var code by remember { mutableStateOf("") }
    var showGuide by remember { mutableStateOf(false) }

    AtuScreen(verticalArrangement = Arrangement.spacedBy(20.dp)) { palette ->
        Spacer(Modifier.height(8.dp))
        AtuHeroCard(
            title = "ATU Campus-a xoş gəldiniz",
            subtitle = "Tələbə hesabınızı təhlükəsiz şəkildə aktivləşdirin.",
            minHeight = 178.dp
        )

        Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Vəsiqə nömrəsi",
                    color = palette.text,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black
                )
                IconButton(onClick = { showGuide = true }) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Təlimat",
                        tint = palette.primary
                    )
                }
            }
            Text(
                text = "Kartınızda olan iş nömrəsini daxil edin.",
                color = palette.muted,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        PremiumCard(radius = 26.dp) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(46.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = palette.primary.copy(alpha = 0.08f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.Badge,
                            contentDescription = null,
                            tint = AtuPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Tələbə vəsiqəsi №",
                        color = palette.text,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Vəsiqənin ön üzündəki nömrəni daxil edin",
                        color = palette.muted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = code,
                onValueChange = { code = it.filter(Char::isDigit).take(7) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                ),
                placeholder = {
                    Text(
                        "0000000",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = palette.primary,
                    unfocusedBorderColor = palette.border,
                    focusedContainerColor = palette.surfaceSoft,
                    unfocusedContainerColor = palette.surfaceSoft,
                    cursorColor = palette.primary
                )
            )
        }

        AnimatedContent(targetState = message, label = "accessMessage") {
            if (it.isNotBlank()) {
                AtuInlineNote(icon = Icons.Outlined.Lock, text = it)
            } else {
                AtuInlineNote(
                    icon = Icons.Outlined.Lock,
                    text = "Məlumatlarınız yalnız rəsmi tələbə datasında yoxlanılır."
                )
            }
        }

        Spacer(Modifier.weight(1f))
        AtuPrimaryButton(
            text = "Davam et",
            enabled = code.length in 5..7,
            loading = loading,
            onClick = { onContinue(code) }
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
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Təlimat",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Vəsiqə nömrəsinin yerini bu nümunədən baxaraq tapa bilərsiniz.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { showGuide = false }) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Bağla",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                    ) {
                        Image(
                            painter = painterResource(R.drawable.student_card_guide),
                            contentDescription = "Tələbə vəsiqəsi nümunəsi",
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.FillWidth
                        )
                    }
                    AtuInlineNote(
                        icon = Icons.Outlined.Info,
                        text = "Nömrəni vəsiqənin ön üzündəki “Tələbə vəsiqəsi №” hissəsindən daxil edin."
                    )
                }
            }
        }
    }
}
