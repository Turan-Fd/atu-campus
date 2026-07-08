package com.atu.campus.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.atu.campus.ui.components.AtuInlineNote
import com.atu.campus.ui.components.AtuInputField
import com.atu.campus.ui.components.AtuLogoHeader
import com.atu.campus.ui.components.AtuPrimaryButton
import com.atu.campus.ui.components.AtuScreen
import com.atu.campus.ui.components.AtuSoftCard

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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Geri", tint = palette.text)
            }
            AtuLogoHeader()
        }

        AtuSoftCard(radius = 30.dp) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier.size(52.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.AdminPanelSettings, contentDescription = null, tint = palette.primary, modifier = Modifier.size(30.dp))
                }
                Text(title, color = palette.text, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            }
            Text(subtitle, color = palette.textSecondary, style = MaterialTheme.typography.bodyMedium)
        }

        AtuSoftCard(radius = 26.dp) {
            Text(
                text = "Secure admin access",
                color = palette.text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            AtuInputField(
                value = password,
                onValueChange = { password = it.take(64) },
                label = "Şifrə",
                placeholder = "Admin şifrəsini daxil edin",
                leadingIcon = Icons.Outlined.Lock,
                trailingIcon = Icons.Outlined.Security,
                visualTransformation = PasswordVisualTransformation()
            )
            AtuInlineNote(
                text = if (message.isNotBlank()) {
                    message
                } else {
                    "Bu giriş yalnız səlahiyyətli inzibatçılar üçündür. Etibarlı giriş məlumatları heç vaxt ekranda göstərilmir."
                }
            )
        }

        Spacer(Modifier.weight(1f))
        AtuPrimaryButton(
            text = ctaText,
            enabled = password.length >= 6,
            loading = loading,
            showArrow = true,
            onClick = { onLogin(password) }
        )
    }
}
