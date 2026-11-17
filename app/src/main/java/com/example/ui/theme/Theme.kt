package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ReversXDarkColorScheme =
  darkColorScheme(
    primary = PlusIconAccent,
    onPrimary = PureBlack,
    secondary = VoiceGradientEnd,
    onSecondary = PureBlack,
    background = PureBlack,
    onBackground = TextPrimary,
    surface = SurfaceElevated,
    onSurface = TextPrimary,
    surfaceVariant = SurfacePill,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceOutline,
  )

@Composable
fun ReversXTheme(content: @Composable () -> Unit) {
  MaterialTheme(
    colorScheme = ReversXDarkColorScheme,
    typography = Typography,
    content = content,
  )
}
