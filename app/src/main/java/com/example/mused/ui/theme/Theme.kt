package com.example.mused.ui.theme


import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color


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
    onSurfaceVariant = MusedTextSecondary
)

private val LightColorScheme = lightColorScheme(
    primary = MusedRed,
    onPrimary = Color.White,
    primaryContainer = MusedLightRed,
    onPrimaryContainer = MusedDarkRed,
    secondary = MusedRed,
    onSecondary = Color.White

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun MuseDTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}