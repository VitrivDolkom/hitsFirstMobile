package com.example.firstmobile.views.layouts.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firstmobile.ui.theme.*
import com.example.firstmobile.viewmodels.CodeBlockViewModel

@Composable
fun OutputConsole(blockViewModel: CodeBlockViewModel) {
    val output by blockViewModel.output.collectAsState()
    
    TopBang(false)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(LocalConfiguration.current.screenHeightDp.dp * 3 / 8)
            .background(color = TextColor)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            itemsIndexed(output.result) { i, str ->
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = NormalPadding)
                ) {
                    items(1) {
                        val isError =
                            output.errorline != -1 && i != 0 && i != output.result.size - 1
                        val text =
                            if (isError) "$str: ${output.errorline}" else str
                        
                        Text(
                            text = text,
                            style = MaterialTheme.typography.body2,
                            color = if (isError) MaterialTheme.colors.error else MainBackground,
                        )
                    }
                }
            }
        }
    }
}