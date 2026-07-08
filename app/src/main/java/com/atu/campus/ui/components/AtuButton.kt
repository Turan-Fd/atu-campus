package com.atu.campus.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.atu.campus.ui.theme.AtuBorder
import com.atu.campus.ui.theme.AtuPrimary
import com.atu.campus.ui.theme.AtuTextPrimary
import com.atu.campus.ui.theme.AtuWhite

@Composable
fun AtuPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    showArrow: Boolean = false,
    icon: ImageVector = Icons.Outlined.ArrowForward
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .atuPressScale(enabled = enabled && !loading),
        enabled = enabled && !loading,
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent,
        shadowElevation = if (enabled && !loading) 8.dp else 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = if (enabled && !loading) {
                        Brush.linearGradient(listOf(AtuPrimary, AtuPrimary.copy(alpha = 0.92f)))
                    } else {
                        Brush.linearGradient(listOf(AtuBorder, AtuBorder))
                    }
                )
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            if (loading) {
                CircularProgressIndicator(color = AtuWhite, strokeWidth = 2.dp)
            } else {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(text = text, color = AtuWhite, fontWeight = FontWeight.SemiBold)
                    if (showArrow) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = AtuWhite,
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AtuSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .atuPressScale(enabled = enabled),
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        color = AtuWhite,
        border = BorderStroke(1.dp, AtuBorder)
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(text = text, color = AtuTextPrimary, fontWeight = FontWeight.SemiBold)
        }
    }
}
