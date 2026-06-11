package com.atu.campus.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.atu.campus.services.SecurityService
import com.atu.campus.ui.components.AtuAnimatedContentWrapper
import com.atu.campus.ui.components.AtuHeroCard
import com.atu.campus.ui.components.AtuInlineNote
import com.atu.campus.ui.components.AtuPermissionCard
import com.atu.campus.ui.components.AtuScreen
import com.atu.campus.ui.components.AtuStatusBadge
import com.atu.campus.ui.theme.AtuMagenta

@Composable
fun PermissionScreen(
    permissionDenied: Boolean,
    onRequestPermission: () -> Unit
) {
    val securityNote = SecurityService().onboardingSecurityNotice()

    AtuScreen(
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 22.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        AtuAnimatedContentWrapper {
            AtuHeroCard(
                title = "T\u0259hl\u00FCk\u0259siz skan",
                subtitle = "T\u0259l\u0259b\u0259 v\u0259siq\u0259si yaln\u0131z cihaz\u0131n\u0131zda emal olunur.",
                minHeight = 154.dp
            ) {
                AtuStatusBadge(
                    text = "M\u0259lumatlar qorunur",
                    icon = Icons.Outlined.VerifiedUser,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }
        }

        AtuAnimatedContentWrapper {
            AtuPermissionCard(
                permissionDenied = permissionDenied,
                onRequestPermission = onRequestPermission,
                securityNote = "Kart \u015F\u0259kill\u0259ri server\u0259 g\u00F6nd\u0259rilmir v\u0259 bu m\u0259rh\u0259l\u0259d\u0259 daimi saxlanm\u0131r."
            )
        }

        AtuInlineNote(
            icon = Icons.Outlined.Lock,
            text = securityNote
        )

        Column(
            modifier = Modifier.padding(horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Outlined.CameraAlt, contentDescription = null, tint = AtuMagenta)
            Text(
                text = "\u0130caz\u0259 yaln\u0131z t\u0259l\u0259b\u0259 kart\u0131n\u0131 oxutmaq \u00FC\u00E7\u00FCnd\u00FCr.",
                color = it.muted,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}
