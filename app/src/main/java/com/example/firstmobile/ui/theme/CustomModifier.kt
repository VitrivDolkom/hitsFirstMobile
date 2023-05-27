package com.example.firstmobile.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

fun Modifier.roundBorder(backColor: Color, borderColor: Color): Modifier =
    border(
        StandardBorder, color = borderColor, shape = BlockShape
    ).roundBackground(backColor)

fun Modifier.roundThinBorder(backColor: Color, borderColor: Color): Modifier =
    border(
        ThinBorder, color = borderColor, shape = BlockShape
    ).roundBackground(backColor)

fun Modifier.roundBackground(backColor: Color): Modifier =
    background(color = backColor, shape = BlockShape)