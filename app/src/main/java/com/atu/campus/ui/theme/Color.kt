package com.atu.campus.ui.theme

// Backward-compatible color aliases while screens migrate to the new token files.
internal object ColorAliases

val AtuPrimary = AtuColors.Primary
val AtuDark = AtuColors.PrimaryDark
val AtuWine = AtuColors.DeepWine
val AtuMagenta = AtuColors.Primary.copy(alpha = 0.82f)
val AtuPlum = AtuColors.SoftPurple
val AtuNavy = AtuColors.DeepWine

val AtuTint = AtuColors.SoftPrimary
val AtuWhite = AtuColors.Surface
val AtuBackground = AtuColors.Background
val AtuSurfaceSoft = AtuColors.SoftSurface
val AtuTextPrimary = AtuColors.TextPrimary
val AtuTextSecondary = AtuColors.TextSecondary
val AtuBorder = AtuColors.Border

val AtuDarkBackground = AtuColors.DarkBackground
val AtuDarkSurface = AtuColors.DarkSurface
val AtuDarkElevated = AtuColors.DarkSoftSurface
val AtuDarkBorder = AtuColors.DarkBorder
val AtuDarkText = AtuColors.DarkText
val AtuDarkMuted = AtuColors.DarkTextSecondary

val AtuSuccess = AtuColors.Success
val AtuWarning = AtuColors.Warning
val AtuDanger = AtuColors.Error
