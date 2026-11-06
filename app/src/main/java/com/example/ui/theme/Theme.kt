package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = NeonCyan,
  onPrimary = Color.Black,
  primaryContainer = Color(0xFF004D53),
  onPrimaryContainer = NeonCyan,
  secondary = NeonPink,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFF59002B),
  onSecondaryContainer = NeonPink,
  tertiary = NeonYellow,
  onTertiary = Color.Black,
  background = ArcadeCanvas,
  onBackground = ArcadeText,
  surface = ArcadeSurface,
  onSurface = ArcadeText,
  surfaceVariant = ArcadeCard,
  onSurfaceVariant = ArcadeTextDim,
  outline = ArcadeBorder
)

private val LightColorScheme = darkColorScheme(
  // We prefer dark energetic arcade canvas by default for games, but keep a crisp variant
  primary = Color(0xFF6200EE),
  onPrimary = Color.White,
  primaryContainer = Color(0xFFEDE7F6),
  onPrimaryContainer = Color(0xFF3700B3),
  secondary = Color(0xFFD81B60),
  onSecondary = Color.White,
  tertiary = Color(0xFFFFB300),
  background = Color(0xFF12121E),
  onBackground = Color(0xFFF0F0F8),
  surface = Color(0xFF1C1B2E),
  onSurface = Color(0xFFF0F0F8),
  surfaceVariant = Color(0xFF28273F),
  onSurfaceVariant = Color(0xFFB0AFC0),
  outline = Color(0xFF3E3D5C)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
