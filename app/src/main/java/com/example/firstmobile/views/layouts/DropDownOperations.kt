package com.example.firstmobile.views.layouts

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firstmobile.R
import com.example.firstmobile.model.CodeBlockOperation
import com.example.firstmobile.ui.theme.BlockShape
import com.example.firstmobile.viewmodels.CodeBlockViewModel
import java.util.*

@Composable
fun DropdownDemo(
    i: Int,
    id: UUID,
    items: List<CodeBlockOperation>,
    operation: CodeBlockOperation,
    viewModel: CodeBlockViewModel
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .height(40.dp)
            .width(40.dp)
            .border(1.dp, color = Color.Black, shape = BlockShape)
            .background(Color.White, shape = BlockShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = operation.symbol,
            modifier = Modifier
                .clickable(onClick = { expanded = true })
                .offset(y = (-3).dp),
            fontSize = 32.sp,
            textAlign = TextAlign.Center
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(
                    Color.White
                )
                .border(1.dp, color = Color.Black)
                .width(60.dp),
            offset = DpOffset(x = (-8).dp, y = 0.dp)
        ) {
            items.forEachIndexed { index, operation ->
                DropdownMenuItem(modifier = Modifier.fillMaxWidth(), onClick = {
                    if (items[index] == CodeBlockOperation.BRACES) {
                        viewModel.changeOperation(i, id, items[index], true)
                    } else {
                        viewModel.changeOperation(i, id, items[index])
                    }
                    
                    expanded = false
                }) {
                    if (operation == CodeBlockOperation.INPUT || operation == CodeBlockOperation.DEFAULT) {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = painterResource(id = R.drawable.trash),
                            contentDescription = "удаление блока"
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = operation.symbol,
                                textAlign = TextAlign.Center,
                                fontSize = 32.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}