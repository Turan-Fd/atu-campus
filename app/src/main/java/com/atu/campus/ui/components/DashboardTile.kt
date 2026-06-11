package com.atu.campus.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.atu.campus.ui.theme.AtuBorder
import com.atu.campus.ui.theme.AtuPrimary
import com.atu.campus.ui.theme.AtuTextPrimary
import com.atu.campus.ui.theme.AtuTextSecondary
import com.atu.campus.ui.theme.AtuTint
import com.atu.campus.ui.theme.AtuWhite

@Composable
fun DashboardTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.08f),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = AtuWhite),
        border = BorderStroke(1.dp, AtuBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AtuPrimary,
                modifier = Modifier
                    .background(AtuTint, RoundedCornerShape(14.dp))
                    .padding(10.dp)
            )
            Text(
                text = title,
                color = AtuTextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = AtuTextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
