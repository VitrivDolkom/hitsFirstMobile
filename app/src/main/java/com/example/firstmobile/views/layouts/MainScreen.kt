package com.example.firstmobile.views.layouts

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
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
                    ) { isHovered, isLeaving ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(
                                    2.dp, color = if (isHovered) Color.Red else Color.Blue, shape = BlockShape
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
                    .background(color = Color.Green, shape = BlockShape), contentAlignment = Alignment.Center
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
                                DropItemLayout(i, block.id, blockViewModel, block.leftBlock, true)
                            }
                        } else {
                            DropItemLayout(i, block.id, blockViewModel, block.leftBlock, true)
                        }
                        
                        if (block.operation.isMathOperation()) {
                            DropdownDemo(i, block.id, block.operation.getVariants(), block.operation, blockViewModel)
                        } else {
                            Text(text = block.operation.symbol, fontSize = 32.sp)
                        }
                        
                        DropItemLayout(i, block.id, blockViewModel, block.rightBlock, false)
                    }
                }
            }
        }
    }
}

@Composable
fun DropdownDemo(
    i: Int, id: UUID, items: List<CodeBlockOperation>, operation: CodeBlockOperation, viewModel: CodeBlockViewModel
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = operation.symbol,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { expanded = true })
                .border(1.dp, color = Color.Black),
            fontSize = 32.sp
        )
        DropdownMenu(
            expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(
                Color.DarkGray
            )
        ) {
            items.forEachIndexed { index, operation ->
                DropdownMenuItem(modifier = Modifier.border(1.dp, color = Color.White), onClick = {
                    viewModel.changeOperation(i, id, items[index])
                    expanded = false
                }) {
                    if (operation == CodeBlockOperation.INPUT || operation == CodeBlockOperation.DEFAULT) {
                        // условие чтобы показать картинку мусорки
                        //Image(painter = , contentDescription = "удаление блока")
                    } else {
                        Text(text = operation.symbol, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun DropItemLayout(i: Int, id: UUID, blockViewModel: CodeBlockViewModel, block: CodeBlock?, isLeftChild: Boolean) {
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
        ) { isHovered, isLeaving ->
            Box(
                modifier = Modifier
                    .height(50.dp)
                    .defaultMinSize(minWidth = 80.dp)
                    .border(
                        1.dp, color = if (isHovered) Color.Red else DarkGreen, shape = BlockShape
                    )
                    .background(color = Color.White, shape = BlockShape)
            ) {}
        }
        
        return
    }
    
    if (block.operation == CodeBlockOperation.INPUT) {
        DropItem(
            i = i,
            id = id,
            isLeftChild = isLeftChild,
            modifier = Modifier
                .height(48.dp)
                .padding(horizontal = 8.dp)
                .background(color = Color.Green, shape = BlockShape),
            blockViewModel = blockViewModel
        ) { isHovered, isLeaving ->
            Box(
                modifier = Modifier
                    .height(48.dp)
                    .defaultMinSize(minWidth = 80.dp)
                    .border(
                        1.dp, color = if (isHovered) Color.Red else DarkGreen, shape = BlockShape
                    )
                    .background(color = Color.White, shape = BlockShape), contentAlignment = Alignment.Center
            ) {
                OutlinedTextField(shape = BlockShape,
                    placeholder = { Text("0") },
                    modifier = Modifier.width(80.dp),
                    value = block.input,
                    onValueChange = { newText ->
                        blockViewModel.updateInput(
                            i, id, newText, isLeftChild
                        )
                    })
            }
        }
        
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
                .background(color = Color.Green, shape = BlockShape), contentAlignment = Alignment.Center
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
                        DropItemLayout(i, block.id, blockViewModel, block.leftBlock, true)
                    }
                } else {
                    DropItemLayout(i, block.id, blockViewModel, block.leftBlock, true)
                }
                
                if (block.operation.isMathOperation()) {
                    DropdownDemo(i, block.id, block.operation.getVariants(), block.operation, blockViewModel)
                } else {
                    Text(text = block.operation.symbol, fontSize = 32.sp)
                }
                
                DropItemLayout(i, block.id, blockViewModel, block.rightBlock, false)
            }
        }
    }
}