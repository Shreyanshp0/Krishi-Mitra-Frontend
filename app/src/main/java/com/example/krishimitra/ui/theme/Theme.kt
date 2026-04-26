package com.example.krishimitra.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val KrishiMitraColorScheme = lightColorScheme(
    primary = DeepGreen,
    onPrimary = White,
    secondary = SoilBrown,
    onSecondary = White,
    background = LightBeige,
    onBackground = DarkCharcoal,
    surface = White,
    onSurface = DarkCharcoal,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = DarkCharcoal,
    outline = SoilBrown,
    tertiary = LeafGreen,
    onTertiary = White
)

@Composable
fun KrishiMitraTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = KrishiMitraColorScheme,
        typography = Typography,
        content = content
    )
}
