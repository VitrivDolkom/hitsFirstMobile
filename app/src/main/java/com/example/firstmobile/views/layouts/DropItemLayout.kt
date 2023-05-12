package com.example.firstmobile.views.layouts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firstmobile.model.Braces
import com.example.firstmobile.model.CodeBlockOperation
import com.example.firstmobile.ui.theme.BlockShape
import com.example.firstmobile.ui.theme.DarkGreen
import com.example.firstmobile.viewmodels.CodeBlockViewModel
import com.example.firstmobile.model.CodeBlock
import com.example.firstmobile.views.draganddrop.DragTarget
import com.example.firstmobile.views.draganddrop.DropItem
import java.util.*

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
        if (block.leftBrace != Braces.DEFAULT) {
            Text(
                text = block.leftBrace.symbol,
                fontSize = 32.sp,
                modifier = Modifier
                    .padding(start = 2.dp)
                    .offset(y = (-3).dp)
            )
        }
        
        DropItem(
            i = i,
            id = id,
            isLeftChild = isLeftChild,
            modifier = Modifier
                .height(48.dp)
                .padding(horizontal = 4.dp)
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
                            i,
                            id,
                            newText,
                            isLeftChild,
                            block.leftBrace,
                            block.rightBrace
                        )
                    })
            }
        }
        
        if (block.rightBrace != Braces.DEFAULT) {
            Text(
                text = block.rightBrace.symbol,
                fontSize = 32.sp,
                modifier = Modifier
                    .padding(end = 2.dp)
                    .offset(y = (-3).dp)
            )
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
                
                if (block.operation.isDropDownable()) {
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