package com.example.firstmobile.views.layouts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firstmobile.model.CodeBlockOperation
import com.example.firstmobile.utils.flowlayouts.FlowRow
import com.example.firstmobile.viewmodels.CodeBlockViewModel
import com.example.firstmobile.views.draganddrop.CodeBlock
import com.example.firstmobile.views.draganddrop.DragTarget

@Composable
fun SheetLayout(blockViewModel: CodeBlockViewModel, openSheet: (BottomSheetScreen) -> Unit) {
    Row(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        FloatingActionButton(onClick = {
            openSheet(BottomSheetScreen.Screen3)
        }) {
            Text(
                text = "?", fontSize = 30.sp
            )  // Процент видимости: ${sheetState.progress.fraction}
        }
        
        FloatingActionButton(onClick = {
            openSheet(BottomSheetScreen.Screen1)
        }) {
            Text(
                text = "+", fontSize = 30.sp
            )  // Процент видимости: ${sheetState.progress.fraction}
        }
        
        FloatingActionButton(onClick = {
            openSheet(BottomSheetScreen.Screen2)
        }) {
            Text(
                text = ">", fontSize = 30.sp
            )  // Процент видимости: ${sheetState.progress.fraction}
        }
    }
}

@Composable
fun DifferentBottomSheets(
    blockViewModel: CodeBlockViewModel,
    currentScreen: BottomSheetScreen,
    onCloseBottomSheet: () -> Unit
) {
    when (currentScreen) {
        BottomSheetScreen.Screen1 -> Screen1(blockViewModel)
        BottomSheetScreen.Screen2 -> Screen2()
        BottomSheetScreen.Screen3 -> Screen3()
    }
}

@Composable
fun Screen1(blockViewModel: CodeBlockViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(CodeBlockOperation.DEFAULT.blocksList()) { i, row ->
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                row.forEach { operation ->
                    val block = CodeBlock(null, operation.symbol, null)
            
                    DragTarget(
                        i = -1, operationToDrop = block, viewModel = blockViewModel
                    ) {
                        Box(
                            modifier = Modifier.height(64.dp).border(
                                    2.dp, color = Color.Red, shape = RoundedCornerShape(15.dp)
                                ), contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier.fillMaxHeight().padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                DropItemLayout(0, block.id, blockViewModel, block.leftBlock, true)
                                Text(text = block.operation)
                                DropItemLayout(0, block.id, blockViewModel, block.rightBlock, false)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Screen2() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .background(Color.Black, shape = RectangleShape)
    ) {
        Text(
            text = "Здесь мог быть ваш результат",
            Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            color = Color.White,
            fontSize = 15.sp
        )
    }
}

@Composable
fun Screen3() {
    val instructions = listOf("Инструкция", "В", "С", "Ы")
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(count = 1) {
            instructions.forEach { instruction ->
                Text(text = instruction, fontSize = 64.sp)
            }
        }
    }
}

sealed class BottomSheetScreen() {
    object Screen1 : BottomSheetScreen()
    object Screen2 : BottomSheetScreen()
    object Screen3 : BottomSheetScreen()
}