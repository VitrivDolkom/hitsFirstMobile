package com.example.firstmobile.views.layouts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firstmobile.ui.theme.BlockShape
import com.example.firstmobile.ui.theme.DarkGreen
import com.example.firstmobile.viewmodels.CodeBlockViewModel
import com.example.firstmobile.views.draganddrop.CodeBlock
import com.example.firstmobile.views.draganddrop.DragTarget
import java.util.UUID

@Composable
fun ArrayEqual(i: Int, block: CodeBlock, blockViewModel: CodeBlockViewModel) {
    Box(modifier = Modifier.padding(4.dp)) {
        DragTarget(
            i = i, operationToDrop = block, viewModel = blockViewModel
        ) {
            Box(
                modifier = Modifier
                    .height(64.dp)
                    .border(
                        2.dp, color = DarkGreen, shape = BlockShape
                    )
                    .background(color = Color.Green, shape = BlockShape), contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .height(48.dp)
                            .defaultMinSize(minWidth = 80.dp)
                            .border(
                                1.dp, color = DarkGreen, shape = BlockShape
                            )
                            .background(color = Color.White, shape = BlockShape), contentAlignment = Alignment.Center
                    ) {
                        OutlinedTextField(shape = BlockShape,
                            placeholder = { Text("0") },
                            modifier = Modifier.width(80.dp),
                            value = block.input,
                            onValueChange = { newText ->
                                blockViewModel.updateInput(
                                    i, block.id, newText, true
                                )
                            })
                    }
                    
                    Text(text = "=", fontSize = 32.sp)
    
                    Box(
                        modifier = Modifier
                            .height(48.dp)
                            .defaultMinSize(minWidth = 80.dp)
                            .border(
                                1.dp, color = DarkGreen, shape = BlockShape
                            )
                            .background(color = Color.White, shape = BlockShape), contentAlignment = Alignment.Center
                    ) {
                        OutlinedTextField(shape = BlockShape,
                            placeholder = { Text("0") },
                            modifier = Modifier.width(80.dp),
                            value = block.input,
                            onValueChange = { newText ->
                                blockViewModel.updateInput(
                                    i, block.id, newText, true
                                )
                            })
                    }
                }
            }
        }
    }
}