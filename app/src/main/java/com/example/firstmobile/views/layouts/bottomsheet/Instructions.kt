package com.example.firstmobile.views.layouts.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firstmobile.R

@Composable
fun Instructions() {
    val instructions = stringArrayResource(id = R.array.instructions)
    
    TopBang(true)
    
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(LocalConfiguration.current.screenHeightDp.dp * 3 / 8),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(items = instructions, itemContent = { str ->
            if (str == stringResource(id = R.string.instruction) || str == stringResource(
                    R.string.author
                )
            ) {
                Text(
                    text = str, fontSize = 20.sp, fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    modifier = Modifier.padding(
                        horizontal = 8.dp, vertical = 8.dp
                    ), text = str, fontSize = 16.sp
                )
            }
        })
    }
}
