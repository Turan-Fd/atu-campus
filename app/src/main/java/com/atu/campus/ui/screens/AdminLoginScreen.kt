package com.atu.campus.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.atu.campus.ui.components.AtuHeroCard
import com.atu.campus.ui.components.AtuInlineNote
import com.atu.campus.ui.components.AtuPrimaryButton
import com.atu.campus.ui.components.AtuScreen
import com.atu.campus.ui.components.PremiumCard

@Composable
fun AdminLoginScreen(
    title: String,
    subtitle: String,
    ctaText: String,
    message: String,
    loading: Boolean,
    onBack: () -> Unit,
    onLogin: (String) -> Unit
) {
    var password by remember { mutableStateOf("") }

    AtuScreen(verticalArrangement = Arrangement.spacedBy(20.dp)) { palette ->
        IconButton(onClick = onBack) {
            Icon(Icons.Outlined.ArrowBack, contentDescription = "Geri", tint = palette.text)
        }
        AtuHeroCard(
            title = title,
            subtitle = subtitle,
            minHeight = 172.dp
        ) {
            Icon(
                Icons.Outlined.AdminPanelSettings,
                contentDescription = null,
                tint = palette.primary,
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
        Text(
            text = "Admin şifrəsi",
            color = palette.text,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black
        )
        PremiumCard(radius = 26.dp) {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it.take(64) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Şifrəni daxil edin") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
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
        if (message.isNotBlank()) AtuInlineNote(text = message)
        Spacer(Modifier.weight(1f))
        AtuPrimaryButton(
            text = ctaText,
            enabled = password.length >= 6,
            loading = loading,
            onClick = { onLogin(password) }
        )
    }
}
