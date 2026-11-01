package com.zeloexe.dnsjantex.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = CyanNeon,
    secondary = EmeraldNeon,
    background = BackgroundObsidian,
    surface = SurfaceDark,
    surfaceVariant = CardBackground,
    onPrimary = BackgroundObsidian,
    onSecondary = BackgroundObsidian,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun DNSJantexTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // DNS Jantex is built with an OLED Dark aesthetic
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
