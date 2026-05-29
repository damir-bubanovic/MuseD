package com.example.mused.ui.theme

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
    onSurfaceVariant = MusedTextSecondary
)

private val LightColorScheme = lightColorScheme(
    primary = MusedRed,
    onPrimary = Color.White,

    primaryContainer = MusedLightRed,
    onPrimaryContainer = MusedDarkRed,

    secondary = MusedRed,
    onSecondary = Color.White
)

@Composable
fun MuseDTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme =
        if (dynamicColor) {
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