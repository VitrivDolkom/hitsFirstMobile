package com.example.firstmobile.views.layouts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firstmobile.model.CodeBlockOperation
import com.example.firstmobile.ui.theme.BlockShape
import com.example.firstmobile.ui.theme.DarkGreen
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
            blockViewModel.execute()
            openSheet(BottomSheetScreen.Screen2)
        }) {
            Text(
                text = ">", fontSize = 30.sp
            )  // Процент видимости: ${sheetState.progress.fraction}
        }
    }
}

@Composable
fun AvailableBlocks(blockViewModel: CodeBlockViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(CodeBlockOperation.DEFAULT.blocksList()) { _, row ->
            FlowRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEach { operation ->
                    val block = CodeBlock(null, operation, null)
                    
                    Box(modifier = Modifier.padding(4.dp)) {
                        DragTarget(
                            i = -1, operationToDrop = block, viewModel = blockViewModel
                        ) {
                            if (block.operation == CodeBlockOperation.INPUT) {
                                Text("Ввод", fontSize = 20.sp)
                            } else {
                                Box(
                                    modifier = Modifier
                                        .height(32.dp)
                                        .border(
                                            2.dp, color = DarkGreen, shape = BlockShape
                                        )
                                        .background(color = Color.Green, shape = BlockShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .padding(4.dp),
                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (!block.operation.isSpecialOperation()) {
                                            DropItemLayout(
                                                -1, block.id, blockViewModel, block.leftBlock, true
                                            )
                                        }
                                        
                                        Text(text = block.operation.symbol)
                                        DropItemLayout(
                                            -1, block.id, blockViewModel, block.rightBlock, false
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OutputConsole(blockViewModel: CodeBlockViewModel) {
    val output by blockViewModel.output.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(Color.Black, shape = RectangleShape)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            itemsIndexed(output) { i, str ->
                LazyRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(1) {
                        Text(text = "$i", modifier = Modifier.padding(horizontal = 8.dp))
                        Text(
                            text = str, color = Color.White, fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Instructions() {
    val instructions = listOf(
        "Инструкция",
        "В нашей IDE все очень просто, жмете на кнопку со знаком '+', для вас высвечивается меню с блоками, далее зажимаете пальцем на блок и перетаскиваете в окошко на экране, после построения кода достаточно нажать на кнопку со знаком '>' и ваш код запустится. Надеемся это вам помогло.",
        "",
        "Авторы:",
        "Дмитрий Волков",
        "Алексей Шумков",
        "Данил Васильев",
        ""
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(count = 1) {
            instructions.forEach { instruction ->
                if (instruction == "Инструкция" || instruction == "Авторы:") {
                    Text(text = instruction, fontSize = 32.sp)
                } else {
                    Text(text = instruction, fontSize = 25.sp)
                }
            }
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
        BottomSheetScreen.Screen1 -> AvailableBlocks(blockViewModel)
        BottomSheetScreen.Screen2 -> OutputConsole(blockViewModel)
        BottomSheetScreen.Screen3 -> Instructions()
    }
}

sealed class BottomSheetScreen {
    object Screen1 : BottomSheetScreen()
    object Screen2 : BottomSheetScreen()
    object Screen3 : BottomSheetScreen()
}