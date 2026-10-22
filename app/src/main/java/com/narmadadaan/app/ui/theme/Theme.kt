package com.narmadadaan.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = BhagwaPrimary,
    onPrimary = Color.White,
    primaryContainer = BhagwaContainer,
    onPrimaryContainer = OnBhagwaContainer,
    secondary = NarmadaBlue,
    onSecondary = Color.White,
    secondaryContainer = NarmadaBlueLight,
    onSecondaryContainer = NarmadaBlueDark,
    tertiary = GoldAccent,
    onTertiary = Color.White,
    background = SacredBgLight,
    onBackground = Color(0xFF1C1917),
    surface = SacredSurface,
    onSurface = Color(0xFF1C1917),
    surfaceVariant = Color(0xFFFFF7ED),
    onSurfaceVariant = Color(0xFF7C2D12),
    outline = SacredBorder,
    error = ErrorRed,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = BhagwaLight,
    onPrimary = Color.White,
    primaryContainer = BhagwaDark,
    onPrimaryContainer = Color.White,
    secondary = NarmadaBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF0C4A6E),
    onSecondaryContainer = Color(0xFFBAE6FD),
    tertiary = GoldAccent,
    onTertiary = Color.White,
    background = DarkBg,
    onBackground = Color(0xFFF5F5F4),
    surface = DarkSurface,
    onSurface = Color(0xFFF5F5F4),
    surfaceVariant = Color(0xFF44403C),
    onSurfaceVariant = Color(0xFFE7E5E4),
    outline = DarkBorder,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun NarmadaDaanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
