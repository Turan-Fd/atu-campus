package com.atu.campus.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = AtuPrimary,
    onPrimary = AtuWhite,
    primaryContainer = AtuTint,
    onPrimaryContainer = AtuWine,
    secondary = AtuDark,
    onSecondary = AtuWhite,
    background = AtuBackground,
    onBackground = AtuTextPrimary,
    surface = AtuWhite,
    onSurface = AtuTextPrimary,
    outline = AtuBorder,
    error = AtuDanger
)

@Composable
fun AtuCampusTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = AtuWine.toArgb()
            window.navigationBarColor = AtuBackground.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AtuTypography,
        content = content
    )
}
