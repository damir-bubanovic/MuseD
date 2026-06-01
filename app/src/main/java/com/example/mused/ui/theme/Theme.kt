package com.example.mused.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = MusedRed,
    onPrimary = Color.White,

    primaryContainer = MusedDarkRed,
    onPrimaryContainer = Color.White,

    secondary = MusedRed,
    onSecondary = Color.White,

    background = MusedBlack,
    onBackground = MusedTextPrimary,

    surface = MusedDarkSurface,
    onSurface = MusedTextPrimary,

    surfaceVariant = MusedSurfaceVariant,
    onSurfaceVariant = MusedTextSecondary,

    outline = Color(0xFFE0D7E8),
    outlineVariant = Color(0xFF5F5865),

    inverseSurface = Color(0xFFE8E0EA),
    inverseOnSurface = Color(0xFF1E1B20),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

private val LightColorScheme = lightColorScheme(
    primary = MusedRed,
    onPrimary = Color.White,

    primaryContainer = MusedLightRed,
    onPrimaryContainer = MusedDarkRed,

    secondary = MusedRed,
    onSecondary = Color.White,

    background = Color.White,
    onBackground = Color(0xFF1C1B1F),

    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),

    surfaceVariant = Color(0xFFF1EEF4),
    onSurfaceVariant = Color(0xFF49454F),

    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0)
)

@Composable
fun MuseDTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val supportsDynamicColor =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val colorScheme =
        if (dynamicColor && supportsDynamicColor) {
            val context = LocalContext.current

            if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        } else if (darkTheme) {
            DarkColorScheme
        } else {
            LightColorScheme
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}