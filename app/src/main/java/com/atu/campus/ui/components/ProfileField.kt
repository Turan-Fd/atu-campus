package com.atu.campus.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.atu.campus.ui.theme.AtuBorder
import com.atu.campus.ui.theme.AtuTextPrimary
import com.atu.campus.ui.theme.AtuTextSecondary

@Composable
fun ProfileField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    emphasize: Boolean = false,
    showDivider: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = AtuTextSecondary,
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            color = AtuTextPrimary,
            fontWeight = if (emphasize) FontWeight.Bold else FontWeight.SemiBold,
            style = if (emphasize) {
                androidx.compose.material3.MaterialTheme.typography.titleLarge
            } else {
                androidx.compose.material3.MaterialTheme.typography.bodyLarge
            },
            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
        )
        if (showDivider) {
            HorizontalDivider(color = AtuBorder)
        }
    }
}
