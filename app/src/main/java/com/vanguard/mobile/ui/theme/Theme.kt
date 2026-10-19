package com.vanguard.mobile.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object VanguardColors {
    val Graphite = Color(0xFF0B0F12)
    val GraphiteRaised = Color(0xFF141A1E)
    val GraphiteSoft = Color(0xFF1D252A)
    val Lime = Color(0xFF9AD93A)
    val LimeStrong = Color(0xFFB4F45A)
    val Ink = Color(0xFFE8EEE6)
    val Muted = Color(0xFFA8B3A5)
    val Warning = Color(0xFFFFC857)
    val Error = Color(0xFFFF6B6B)
}

private val VanguardScheme = darkColorScheme(
    primary = VanguardColors.Lime,
    onPrimary = Color(0xFF101600),
    primaryContainer = Color(0xFF263B0D),
    onPrimaryContainer = VanguardColors.LimeStrong,
    secondary = Color(0xFF8AC7D9),
    background = VanguardColors.Graphite,
    onBackground = VanguardColors.Ink,
    surface = VanguardColors.GraphiteRaised,
    onSurface = VanguardColors.Ink,
    surfaceVariant = VanguardColors.GraphiteSoft,
    onSurfaceVariant = VanguardColors.Muted,
    error = VanguardColors.Error,
)

@Composable
fun VanguardTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = VanguardScheme, content = content)
}
