package com.example.firstmobile.views.layouts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firstmobile.model.CodeBlockOperation
import com.example.firstmobile.ui.theme.BlockShape
import com.example.firstmobile.ui.theme.DarkGreen
import com.example.firstmobile.viewmodels.CodeBlockViewModel
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
        Text(text = "$test", fontSize = 0.sp)
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
                            .padding(4.dp)
                            .height(64.dp),
                        blockViewModel = blockViewModel
                    ) { isHovered, _ ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(
                                    1.dp,
                                    color = if (isHovered) Color.Red else Color.Blue,
                                    shape = BlockShape
                                )
                                .background(Color.White, shape = BlockShape)
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
                        if (block.operation.isSpecialOperation()) {
                            Box(
                                modifier = Modifier
                                    .width(0.dp)
                                    .height(0.dp)
                            ) {
                                DropItemLayout(
                                    i,
                                    block.id,
                                    blockViewModel,
                                    block.leftBlock,
                                    true
                                )
                            }
                        } else {
                            DropItemLayout(
                                i,
                                block.id,
                                blockViewModel,
                                block.leftBlock,
                                true
                            )
                        }
                        
                        if (block.operation.isMathOperation()) {
                            DropdownDemo(
                                i,
                                block.id,
                                block.operation.getVariants(),
                                block.operation,
                                blockViewModel
                            )
                        } else {
                            Text(
                                text = block.operation.symbol, fontSize = 32.sp
                            )
                        }
                        
                        DropItemLayout(
                            i,
                            block.id,
                            blockViewModel,
                            block.rightBlock,
                            false,
                            block.operation == CodeBlockOperation.ARRAY_EQUAL
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DropItemLayout(
    i: Int,
    id: UUID,
    blockViewModel: CodeBlockViewModel,
    block: CodeBlock?,
    isLeftChild: Boolean,
    isArray: Boolean = false
) {
    if (block == null) {
        DropItem(
            i = i,
            id = id,
            isLeftChild = isLeftChild,
            modifier = Modifier
                .height(48.dp)
                .padding(horizontal = 8.dp)
                .background(color = Color.White, shape = BlockShape),
            blockViewModel = blockViewModel
        ) { isHovered, _ ->
            Box(
                modifier = Modifier
                    .height(50.dp)
                    .defaultMinSize(minWidth = 80.dp)
                    .border(
                        1.dp,
                        color = if (isHovered) Color.Red else DarkGreen,
                        shape = BlockShape
                    )
                    .background(color = Color.White, shape = BlockShape)
            ) {}
        }
        
        return
    }
    
    if (block.operation == CodeBlockOperation.INPUT) {
        if (isArray) Text(text = "[", fontSize = 32.sp, modifier = Modifier.padding(start = 4.dp))
        
        DropItem(
            i = i,
            id = id,
            isLeftChild = isLeftChild,
            modifier = Modifier
                .height(48.dp)
                .padding(horizontal = if (isArray) 2.dp else 8.dp)
                .background(color = Color.Green, shape = BlockShape),
            blockViewModel = blockViewModel
        ) { isHovered, _ ->
            Box(
                modifier = Modifier
                    .height(48.dp)
                    .border(
                        1.dp,
                        color = if (isHovered) Color.Red else DarkGreen,
                        shape = BlockShape
                    )
                    .background(color = Color.White, shape = BlockShape),
                contentAlignment = Alignment.Center
            ) {
                OutlinedTextField(shape = BlockShape,
                    modifier = Modifier.width(if (isArray) 160.dp else 80.dp),
                    value = block.input,
                    onValueChange = { newText ->
                        blockViewModel.updateInput(
                            i, id, newText, isLeftChild
                        )
                    })
            }
        }
        
        if (isArray) Text(text = "]", fontSize = 32.sp)
        
        return
    }
    
    // обычный случай, когда блок есть и он не input
    DragTarget(
        i = i, operationToDrop = block, viewModel = blockViewModel
    ) {
        Box(
            modifier = Modifier
                .height(64.dp)
                .padding(horizontal = 8.dp)
                .border(
                    2.dp, color = DarkGreen, shape = BlockShape
                )
                .background(color = Color.Green, shape = BlockShape),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (block.operation.isSpecialOperation()) {
                    Box(
                        modifier = Modifier
                            .width(0.dp)
                            .height(0.dp)
                    ) {
                        DropItemLayout(
                            i, block.id, blockViewModel, block.leftBlock, true
                        )
                    }
                } else {
                    DropItemLayout(
                        i, block.id, blockViewModel, block.leftBlock, true
                    )
                }
                
                if (block.operation.isMathOperation()) {
                    DropdownDemo(
                        i,
                        block.id,
                        block.operation.getVariants(),
                        block.operation,
                        blockViewModel
                    )
                } else {
                    Text(text = block.operation.symbol, fontSize = 32.sp)
                }
                
                DropItemLayout(
                    i, block.id, blockViewModel, block.rightBlock, false
                )
            }
        }
    }
}