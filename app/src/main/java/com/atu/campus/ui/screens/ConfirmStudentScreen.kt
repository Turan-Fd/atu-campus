package com.atu.campus.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.atu.campus.ui.components.AppScreen
import com.atu.campus.ui.components.AtuPrimaryButton
import com.atu.campus.ui.components.AtuSecondaryButton
import com.atu.campus.ui.components.PremiumCard
import com.atu.campus.ui.components.PremiumNote
import com.atu.campus.ui.components.SectionHeader
import com.atu.campus.ui.components.StatusBadge
import com.atu.campus.ui.theme.AtuBorder
import com.atu.campus.ui.theme.AtuPrimary
import com.atu.campus.ui.theme.AtuSurfaceSoft
import com.atu.campus.ui.theme.AtuTextSecondary

@Composable
fun ConfirmStudentScreen(
    cardNumber: String?,
    message: String,
    onLookup: (String) -> Unit,
    onRescan: () -> Unit
) {
    var number by remember { mutableStateOf("") }

    LaunchedEffect(cardNumber) {
        number = cardNumber.orEmpty()
    }

    val canContinue = number.length == 6

    AppScreen(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(4.dp))
        StatusBadge(text = "N\u00F6mr\u0259ni yoxlay\u0131n", icon = Icons.Outlined.Info)
        SectionHeader(
            title = "V\u0259siq\u0259 n\u00F6mr\u0259si",
            subtitle = "OCR n\u0259tic\u0259sini yoxlay\u0131n. Yaln\u0131z t\u0259l\u0259b\u0259 v\u0259siq\u0259sind\u0259ki i\u015F n\u00F6mr\u0259sini daxil edin."
        )

        PremiumCard(radius = 28.dp) {
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Icon(Icons.Outlined.Badge, contentDescription = null, tint = AtuPrimary)
                Text(
                    "T\u0259l\u0259b\u0259 datas\u0131nda axtar\u0131\u015F",
                    color = it.text,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black
                )
            }

            Text(
                text = "T\u0259l\u0259b\u0259 v\u0259siq\u0259si №",
                color = AtuTextSecondary,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = number,
                onValueChange = { value -> number = value.filter(Char::isDigit).take(6) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("M\u0259s\u0259l\u0259n: 193253") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AtuPrimary,
                    unfocusedBorderColor = AtuBorder,
                    focusedContainerColor = AtuSurfaceSoft,
                    unfocusedContainerColor = AtuSurfaceSoft,
                    cursorColor = AtuPrimary
                )
            )
        }

        if (message.isNotBlank()) {
            PremiumNote(text = message)
        }
        PremiumNote(
            text = "M\u0259lumatlar yaln\u0131z n\u00F6mr\u0259 dataset-d\u0259 unikal uy\u011Funluq verdikd\u0259 profil\u0259 yaz\u0131l\u0131r. Uy\u011Funluq yoxdursa yanl\u0131\u015F account a\u00E7\u0131lm\u0131r."
        )

        AtuPrimaryButton(
            text = "N\u00F6mr\u0259ni yoxla v\u0259 davam et",
            enabled = canContinue,
            onClick = { onLookup(number) }
        )
        AtuSecondaryButton(text = "Yenid\u0259n skan et", onClick = onRescan)
        Spacer(Modifier.height(10.dp))
    }
}
