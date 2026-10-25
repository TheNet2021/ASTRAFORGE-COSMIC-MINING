package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AstraforgeColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = Color.Black,
    secondary = CosmicPurple,
    onSecondary = Color.White,
    tertiary = StellarGold,
    onTertiary = Color.Black,
    background = SpaceBlack,
    onBackground = LightSlate,
    surface = DeepNavy,
    onSurface = LightSlate,
    error = SolarRed,
    onError = Color.White
)

@Composable
fun AstraforgeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AstraforgeColorScheme,
        typography = Typography,
        content = content
    )
}
