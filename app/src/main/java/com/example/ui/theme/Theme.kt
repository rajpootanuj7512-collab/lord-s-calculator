package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = DeepBlack,
    secondary = HyperPink,
    onSecondary = TextWhite,
    tertiary = CosmicGold,
    background = DeepBlack,
    onBackground = TextWhite,
    surface = CyberSlate,
    onSurface = TextWhite,
    surfaceVariant = DarkGlass,
    onSurfaceVariant = GhostGray
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force sci-fi dark theme
    dynamicColor: Boolean = false, // Use our consistent glowing colors
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
