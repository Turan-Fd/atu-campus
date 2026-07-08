package com.atu.campus.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = AtuColors.Primary,
    onPrimary = AtuColors.Surface,
    primaryContainer = AtuColors.SoftPrimary,
    onPrimaryContainer = AtuColors.PrimaryDark,
    secondary = AtuColors.PrimaryDark,
    onSecondary = AtuColors.Surface,
    background = AtuColors.Background,
    onBackground = AtuColors.TextPrimary,
    surface = AtuColors.Surface,
    onSurface = AtuColors.TextPrimary,
    surfaceVariant = AtuColors.SoftSurface,
    onSurfaceVariant = AtuColors.TextSecondary,
    outline = AtuColors.Border,
    error = AtuColors.Error
)

private val DarkColors = darkColorScheme(
    primary = AtuPrimary,
    onPrimary = AtuColors.Surface,
    primaryContainer = AtuColors.PrimaryDark,
    onPrimaryContainer = AtuColors.Surface,
    secondary = AtuColors.Warning,
    onSecondary = AtuColors.DeepWine,
    background = AtuColors.DarkBackground,
    onBackground = AtuColors.DarkText,
    surface = AtuColors.DarkSurface,
    onSurface = AtuColors.DarkText,
    surfaceVariant = AtuColors.DarkSoftSurface,
    onSurfaceVariant = AtuColors.DarkTextSecondary,
    outline = AtuColors.DarkBorder,
    error = AtuColors.Error
)

@Composable
fun AtuCampusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = if (darkTheme) AtuColors.DarkBackground.toArgb() else AtuColors.Background.toArgb()
            window.navigationBarColor = if (darkTheme) AtuColors.DarkBackground.toArgb() else AtuColors.Background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AtuTypography,
        content = content
    )
}
