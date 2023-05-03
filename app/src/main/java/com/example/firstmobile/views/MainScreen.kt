package com.example.firstmobile.views

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import com.example.firstmobile.viewmodels.CodeBlockViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firstmobile.model.CodeBlockOperation

@Composable
fun MainScreen(
    blockViewModel: CodeBlockViewModel, openSheet: (BottomSheetScreen) -> Unit
) {
    MainContent(blockViewModel = blockViewModel, openSheet = openSheet)
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp)
        ) {
            itemsIndexed(blockViewModel.addedBlocks) { i, row ->
                LazyRow(
                    modifier = Modifier.padding(8.dp)
                ) {
                    itemsIndexed(row) { j, _ ->
                        DropItem(
                            i = i,
                            j = j,
                            modifier = Modifier
                                .width(80.dp)
                                .height(64.dp)
                                .background(Color.White)
                                .padding(end = 8.dp),
                            blockViewModel = blockViewModel
                        ) { isHovered, _, isAvailable, operation ->
                            if (!isAvailable) { // пока недоступная клетка
                            } else if (operation != CodeBlockOperation.DEFAULT) {
                                DragTarget(
                                    i = i, j = j, operationToDrop = operation, viewModel = blockViewModel
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(80.dp)
                                            .height(64.dp)
                                            .border(
                                                3.dp,
                                                color = if (isHovered) Color.Blue else Color.Red,
                                                shape = RoundedCornerShape(15.dp)
                                            ), contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = operation.symbol.toString(),
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .border(
                                            3.dp,
                                            color = if (isHovered) Color.Blue else Color.Red,
                                            shape = RoundedCornerShape(15.dp)
                                        ), contentAlignment = Alignment.Center
                                ) {
                                
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}