package com.example.firstmobile.views.layouts

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import com.example.firstmobile.viewmodels.CodeBlockViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.firstmobile.views.draganddrop.CodeBlock
import com.example.firstmobile.views.draganddrop.DragTarget
import com.example.firstmobile.views.draganddrop.DropItem

@Composable
fun MainScreen(
    blockViewModel: CodeBlockViewModel, openSheet: (BottomSheetScreen) -> Unit
) {
    SheetLayout(blockViewModel = blockViewModel, openSheet = openSheet)
    
    val blocks by blockViewModel.blocks.collectAsState()
    val test by blockViewModel.test.collectAsState()
    
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(text = "$test")
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp)
        ) {
            itemsIndexed(blocks) { i, block ->
                if (block.operation == "") {
                    DropItem(
                        i = i,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .background(Color.Blue),
                        blockViewModel = blockViewModel
                    ) { _, _ ->
                        Text(text = "empty")
                    }
                } else {
                    DragTarget(
                        i = i, operationToDrop = block, viewModel = blockViewModel
                    ) {
                        Box(
                            modifier = Modifier
                                .height(64.dp)
                                .border(
                                    3.dp, color = Color.Red, shape = RoundedCornerShape(15.dp)
                                ), contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                DropItemLayout(i, blockViewModel, block.leftBlock)
                                Text(text = block.operation)
                                DropItemLayout(i, blockViewModel, block.rightBlock)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DropItemLayout(i: Int, blockViewModel: CodeBlockViewModel, block: CodeBlock?) {
    if (block != null) {
        DragTarget(
            i = i, operationToDrop = block, viewModel = blockViewModel
        ) {
            Box(
                modifier = Modifier
                    .height(64.dp)
                    .border(
                        3.dp, color = Color.Red, shape = RoundedCornerShape(15.dp)
                    ), contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DropItemLayout(i, blockViewModel, block.leftBlock)
                    Text(text = block.operation)
                    DropItemLayout(i, blockViewModel, block.rightBlock)
                }
            }
        }
    } else {
        DropItem(
            i = i, modifier = Modifier
                .height(64.dp)
                .background(Color.White), blockViewModel = blockViewModel
        ) { _, _ ->
            Text(text = "empty")
        }
    }
}