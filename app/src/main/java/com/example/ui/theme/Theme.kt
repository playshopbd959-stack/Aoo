package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val XsosColorScheme = darkColorScheme(
    primary = PureWhite,
    onPrimary = PureBlack,
    primaryContainer = SurfaceVariantDark,
    onPrimaryContainer = PureWhite,
    secondary = OffWhite,
    onSecondary = PureBlack,
    secondaryContainer = CardBorder,
    onSecondaryContainer = OffWhite,
    tertiary = CyberCyan,
    onTertiary = PureBlack,
    background = PureBlack,
    onBackground = OffWhite,
    surface = CyberDark,
    onSurface = OffWhite,
    surfaceVariant = SurfaceDark,
    onSurfaceVariant = TextMuted,
    outline = CardBorder,
    outlineVariant = CardBorderActive,
    error = CyberRed,
    onError = PureBlack
)

@Composable
fun XSOSTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = XsosColorScheme,
        typography = Typography,
        content = content
    )
}
