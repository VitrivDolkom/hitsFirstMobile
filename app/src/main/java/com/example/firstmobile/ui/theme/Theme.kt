package com.example.firstmobile.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = DarkBlockBackground,
    secondary = MainBackground,
    background = TextColor,
    error = HoverRed,
    surface = DarkGreen
)

private val LightColorPalette = lightColors(
    primary = BlockBackground,
    secondary = TextColor,
    background = MainBackground,
    surface = DarkGreen,
    error = HoverRed,
    primaryVariant = BorderBlue
)

@Composable
fun InterpreterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }
    
    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}