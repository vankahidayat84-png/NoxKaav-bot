package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = MaroonPrimary,
    onPrimary = WhiteText,
    secondary = LighterGray,
    onSecondary = WhiteText,
    background = DarkGrayBackground,
    onBackground = WhiteText,
    surface = DarkGrayBackground,
    onSurface = WhiteText,
    surfaceVariant = LighterGray,
    onSurfaceVariant = WhiteText,
    tertiary = MaroonPrimary,
    onTertiary = WhiteText
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
