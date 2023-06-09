package com.example.firstmobile.views.layouts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.firstmobile.model.Braces
import com.example.firstmobile.model.CodeBlock
import com.example.firstmobile.model.CodeBlockOperation
import com.example.firstmobile.ui.theme.*
import com.example.firstmobile.viewmodels.CodeBlockViewModel
import com.example.firstmobile.views.draganddrop.DragTarget
import com.example.firstmobile.views.draganddrop.DropItem
import java.util.*

@Composable
fun BlockPartText(str: String) {
    Text(
        text = str,
        modifier = Modifier
            .padding(end = MicroPadding)
            .offset(y = (-3).dp),
        style = MaterialTheme.typography.h3,
        color = TextColor
    )
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
                .height(MiddleHeight)
                .padding(horizontal = NormalPadding)
                .roundBackground(MaterialTheme.colors.background),
            blockViewModel = blockViewModel
        ) { isHovered, _ ->
            Box(
                modifier = Modifier
                    .height(50.dp)
                    .defaultMinSize(minWidth = 80.dp)
                    .roundThinBorder(
                        backColor = MaterialTheme.colors.background,
                        borderColor = if (isHovered) MaterialTheme.colors.error else MaterialTheme.colors.surface
                    )
            ) {}
        }
        
        return
    }
    
    if (block.operation == CodeBlockOperation.INPUT) {
        if (block.leftBrace != Braces.DEFAULT) {
            BlockPartText(str = block.leftBrace.symbol)
        }
        
        DropItem(
            i = i,
            id = id,
            isLeftChild = isLeftChild,
            modifier = Modifier
                .height(MiddleHeight)
                .padding(horizontal = MiddlePadding)
                .roundBackground(MaterialTheme.colors.primary),
            blockViewModel = blockViewModel
        ) { isHovered, _ ->
            Box(
                modifier = Modifier
                    .height(MiddleHeight)
                    .roundThinBorder(
                        backColor = MaterialTheme.colors.background,
                        borderColor = if (isHovered) MaterialTheme.colors.error else MaterialTheme.colors.surface
                    ), contentAlignment = Alignment.Center
            ) {
                DisableSelection {
                    OutlinedTextField(
                        textStyle = TextStyle(color = MaterialTheme.colors.secondary),
                        shape = BlockShape,
                        modifier = Modifier.width(if (isArray) BigInputWidth else InputWidth),
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
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = MaterialTheme.colors.background,
                            cursorColor = MaterialTheme.colors.secondary
                        ),
                    )
                }
            }
        }
        
        if (block.rightBrace != Braces.DEFAULT) {
            BlockPartText(str = block.rightBrace.symbol)
        }
        
        return
    }
    
    DragTarget(
        i = i, operationToDrop = block
    ) {
        Box(
            modifier = Modifier
                .height(BigHeight)
                .padding(horizontal = NormalPadding)
                .roundBorder(
                    backColor = MaterialTheme.colors.primary,
                    borderColor = MaterialTheme.colors.surface
                ), contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = NormalPadding),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                
                if (block.leftBrace != Braces.DEFAULT) {
                    BlockPartText(str = block.leftBrace.symbol)
                }
                
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
                
                if (block.operation.isDropDown()) {
                    DropdownDemo(
                        i,
                        block.id,
                        block.operation.getVariants(),
                        block.operation,
                        blockViewModel
                    )
                } else {
                    Text(
                        text = block.operation.symbol,
                        style = MaterialTheme.typography.h3,
                        color = TextColor
                    )
                }
                
                DropItemLayout(
                    i, block.id, blockViewModel, block.rightBlock, false
                )
                
                if (block.rightBrace != Braces.DEFAULT) {
                    BlockPartText(str = block.rightBrace.symbol)
                }
            }
        }
    }
}