package com.atu.campus.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val AtuFontFamily = FontFamily.SansSerif

private fun atuTextStyle(
    size: Int,
    lineHeight: Int,
    weight: FontWeight
) = TextStyle(
    fontFamily = AtuFontFamily,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
    fontWeight = weight,
    platformStyle = PlatformTextStyle(includeFontPadding = false)
)

val AtuTypography = Typography(
    displaySmall = atuTextStyle(28, 32, FontWeight.Bold),
    headlineLarge = atuTextStyle(28, 34, FontWeight.SemiBold),
    headlineMedium = atuTextStyle(24, 30, FontWeight.SemiBold),
    headlineSmall = atuTextStyle(18, 24, FontWeight.SemiBold),
    titleLarge = atuTextStyle(18, 24, FontWeight.SemiBold),
    titleMedium = atuTextStyle(16, 22, FontWeight.SemiBold),
    bodyLarge = atuTextStyle(14, 21, FontWeight.Normal),
    bodyMedium = atuTextStyle(14, 20, FontWeight.Normal),
    bodySmall = atuTextStyle(12, 17, FontWeight.Normal),
    labelLarge = atuTextStyle(11, 14, FontWeight.Medium),
    labelMedium = atuTextStyle(10, 13, FontWeight.Medium),
    labelSmall = atuTextStyle(10, 12, FontWeight.Medium)
)
