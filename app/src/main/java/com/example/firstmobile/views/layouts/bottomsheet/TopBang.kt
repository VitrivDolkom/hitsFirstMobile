package com.example.firstmobile.views.layouts.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.firstmobile.R
import com.example.firstmobile.ui.theme.MainBackground
import com.example.firstmobile.ui.theme.TextColor

@Composable
fun TopBang(isLight: Boolean = true) {
    val boxColor = if (isLight) MaterialTheme.colors.background else TextColor
    val bangColor = if (isLight) MaterialTheme.colors.secondary else MainBackground
    
    Box(
        modifier = Modifier
            .background(color = boxColor)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = R.string.bang),
            color = bangColor,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
    }
}