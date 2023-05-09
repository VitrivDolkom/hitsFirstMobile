package com.example.firstmobile.views.layouts

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import com.example.firstmobile.viewmodels.CodeBlockViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firstmobile.model.CodeBlockOperation
import com.example.firstmobile.ui.theme.BlockShape
import com.example.firstmobile.ui.theme.DarkGreen
import com.example.firstmobile.views.draganddrop.CodeBlock
import com.example.firstmobile.views.draganddrop.DragTarget
import com.example.firstmobile.views.draganddrop.DropItem
import java.util.*

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
        Text(text= "$test", fontSize=0.sp)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp)
        ) {
            itemsIndexed(blocks) { i, block ->
                if (block.operation == CodeBlockOperation.DEFAULT) {
                    DropItem(
                        i = i,
                        id = block.id,
                        isLeftChild = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp), blockViewModel = blockViewModel
                    ) { isHovered, _ ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(
                                    1.dp,
                                    color = if (isHovered) Color.Red else Color.Blue,
                                    shape = BlockShape
                                ).background(Color.White, shape = BlockShape)
                        ) {}
                    }
                } else {
                    SingleBlock(blockViewModel, block, i)
                }
            }
        }
    }
}

@Composable
fun SingleBlock(blockViewModel: CodeBlockViewModel, block: CodeBlock, i: Int) {
    Box(modifier=Modifier.padding(4.dp)){
    DragTarget(
        i = i, operationToDrop = block, viewModel = blockViewModel
    ) {
        Box(
            modifier = Modifier
                .height(64.dp)
                .border(
                    2.dp, color = DarkGreen, shape = BlockShape
                )
                .background(color = Color.Green, shape = BlockShape),
            contentAlignment = Alignment.Center
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(1) {
                    if (!block.operation.isSpecialOperation()) {
                        DropItemLayout(i, block.id, blockViewModel, block.leftBlock, true)
                    } else if (block.operation == CodeBlockOperation.EQUAL) {
                        TextField(value = "", onValueChange = { })
                    }

                    Text(text = block.operation.symbol, fontSize=32.sp)
                    DropItemLayout(i, block.id, blockViewModel, block.rightBlock, false)
                }
            }
        }
    }
    }
}

@Composable
fun DropItemLayout(i: Int, id: UUID, blockViewModel: CodeBlockViewModel, block: CodeBlock?, isLeftChild: Boolean) {
    if (block != null) {
        DragTarget(
            i = i, operationToDrop = block, viewModel = blockViewModel
        ) {
            Box(
                modifier = Modifier
                    .height(64.dp)
                    .padding(horizontal = 8.dp)
                    .border(
                        2.dp, color = DarkGreen, shape = BlockShape
                    ).background(color = Color.Green, shape = BlockShape),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!block.operation.isSpecialOperation()) {
                        DropItemLayout(i, block.id, blockViewModel, block.leftBlock, true)
                    } else if (block.operation == CodeBlockOperation.EQUAL) {
                        TextField(value = "", onValueChange = {  })
                    }

                    Text(text = block.operation.symbol, fontSize = 32.sp)
                    DropItemLayout(i, block.id, blockViewModel, block.rightBlock, false)
                }
            }
        }
    } else {
        DropItem(
            i = i,
            id = id,
            isLeftChild = isLeftChild,
            modifier = Modifier
                .height(48.dp)
                .padding(horizontal = 8.dp)
                .background(Color.White, shape = BlockShape),
            blockViewModel = blockViewModel
        ) { isHovered, _ ->
            Box(
                modifier = Modifier
                    .height(48.dp)
                    .defaultMinSize(minWidth = 80.dp)
                    .border(
                        1.dp, color = if (isHovered) Color.Red else DarkGreen, shape = BlockShape
                    )
                    .background(color=Color.White, shape = BlockShape)
            ) {}
        }
    }
}