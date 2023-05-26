package com.example.firstmobile.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

fun Modifier.roundBorder(backColor: Color, borderColor: Color): Modifier = border(
    2.dp, color = borderColor, shape = BlockShape
).roundBackground(backColor)

fun Modifier.roundThickBorder(backColor: Color, borderColor: Color): Modifier = border(
    1.dp, color = borderColor, shape = BlockShape
).roundBackground(backColor)

fun Modifier.roundBackground(backColor: Color): Modifier =
    background(color = backColor, shape = BlockShape)