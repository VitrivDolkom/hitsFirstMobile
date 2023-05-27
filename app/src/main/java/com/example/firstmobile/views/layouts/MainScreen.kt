package com.example.firstmobile.views.layouts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firstmobile.model.CodeBlock
import com.example.firstmobile.model.CodeBlockOperation
import com.example.firstmobile.ui.theme.*
import com.example.firstmobile.viewmodels.CodeBlockViewModel
import com.example.firstmobile.views.draganddrop.DragTarget
import com.example.firstmobile.views.draganddrop.DropItem
import com.example.firstmobile.views.layouts.bottomsheet.BottomSheetScreen
import com.example.firstmobile.views.layouts.bottomsheet.SheetLayout

@Composable
fun MainScreen(
    blockViewModel: CodeBlockViewModel, openSheet: (BottomSheetScreen) -> Unit
) {
    val blocks by blockViewModel.blocks.collectAsState()
    val changesNum by blockViewModel.changesNum.collectAsState()
    
    SheetLayout(blockViewModel = blockViewModel, openSheet = openSheet)
    
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(LocalConfiguration.current.screenHeightDp.dp * 7 / 8)
                .verticalScroll(rememberScrollState())
        ) {
            for(it in 0..blocks.size) {
                if (it >= blocks.size) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(LocalConfiguration.current.screenHeightDp.dp * 1 / 2)
                    ) {}
                } else {
                    val block = blocks[it]
                    
                    if (block.operation == CodeBlockOperation.DEFAULT) {
                        DropItem(
                            i = it,
                            id = block.id,
                            isLeftChild = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SmallPadding)
                                .height(BigHeight),
                            blockViewModel = blockViewModel
                        ) { isHovered, _ ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .roundThinBorder(
                                        backColor = MaterialTheme.colors.background,
                                        borderColor = if (isHovered) MaterialTheme.colors.error else MaterialTheme.colors.primaryVariant
                                    )
                            ) {}
                        }
                    } else {
                        SingleBlock(blockViewModel, block, it)
                    }
                }
            }
        }
        Text(text = "$changesNum", fontSize = 0.sp)
    }
}

@Composable
fun SingleBlock(blockViewModel: CodeBlockViewModel, block: CodeBlock, i: Int) {
    Box(modifier = Modifier.padding(SmallPadding)) {
        DragTarget(
            i = i, operationToDrop = block
        ) {
            Box(
                modifier = Modifier
                    .height(BigHeight)
                    .roundBorder(
                        backColor = MaterialTheme.colors.primary,
                        borderColor = MaterialTheme.colors.surface
                    ), contentAlignment = Alignment.Center
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = NormalPadding),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(1) {
                        if (block.operation.isSpecialOperation() || block.operation.isEmptyBlock()) {
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
                        
                        if (block.operation.isDropDown()) {
                            DropdownDemo(
                                i,
                                block.id,
                                block.operation.getVariants(),
                                block.operation,
                                blockViewModel
                            )
                        } else {
                            DisableSelection {
                                Text(
                                    text = block.operation.symbol,
                                    style = MaterialTheme.typography.h3,
                                    color = TextColor
                                )
                            }
                        }
                        
                        if (block.operation.isEmptyBlock()) {
                            Box(
                                modifier = Modifier.size(EmptySize)
                            ) {}
                        } else {
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
}
