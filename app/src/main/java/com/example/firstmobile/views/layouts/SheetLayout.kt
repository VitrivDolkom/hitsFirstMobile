package com.example.firstmobile.views.layouts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firstmobile.R
import com.example.firstmobile.model.CodeBlockOperation
import com.example.firstmobile.ui.theme.*
import com.example.firstmobile.utils.flowlayouts.FlowRow
import com.example.firstmobile.viewmodels.CodeBlockViewModel
import com.example.firstmobile.model.CodeBlock
import com.example.firstmobile.views.draganddrop.DragTarget

@Composable
fun SheetLayout(
    blockViewModel: CodeBlockViewModel, openSheet: (BottomSheetScreen) -> Unit
) {
    Row(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        FloatingActionButton(backgroundColor = BlueButton, onClick = {
            openSheet(BottomSheetScreen.Screen3)
        }) {
            Text(
                text = stringResource(id = R.string.question),
                fontSize = 30.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        FloatingActionButton(backgroundColor = RedButton, onClick = {
            openSheet(BottomSheetScreen.Screen1)
        }) {
            Text(
                text = stringResource(id = R.string.blocks),
                fontSize = 30.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        FloatingActionButton(backgroundColor = GreenButton, onClick = {
            blockViewModel.execute()
            openSheet(BottomSheetScreen.Screen2)
        }) {
            Text(
                text = stringResource(id = R.string.run),
                fontSize = 30.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AvailableBlocks(blockViewModel: CodeBlockViewModel) {

    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = R.string.bang),
            color = Color.Black,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(LocalConfiguration.current.screenHeightDp.dp * 3 / 8)
            .padding(vertical = 16.dp, horizontal = 4.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(CodeBlockOperation.DEFAULT.blocksList()) { _, row ->
            FlowRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(text = "▸ " + row.name, fontSize = 16.sp)
                }
                row.listToShow.forEach { operation ->
                    val block = CodeBlock(null, operation, null)

                    Box(modifier = Modifier.padding(4.dp)) {
                        DragTarget(
                            i = -1,
                            operationToDrop = block,
                            viewModel = blockViewModel
                        ) {

                            Box(
                                modifier = Modifier
                                    .height(32.dp)
                                    .border(
                                        2.dp,
                                        color = DarkGreen,
                                        shape = BlockShape
                                    )
                                    .background(
                                        color = Color.Green, shape = BlockShape
                                    ), contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .padding(4.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (!block.operation.isSpecialOperation() && !block.operation.isEmptyBlock()) {
                                        DropItemLayout(
                                            -1,
                                            block.id,
                                            blockViewModel,
                                            block.leftBlock,
                                            true
                                        )
                                    }

                                    var operationText = block.operation.symbol
                                    if (block.operation == CodeBlockOperation.ARRAY_EQUAL) operationText += " [ ]"

                                    Text(text = operationText)
                                    DropItemLayout(
                                        -1,
                                        block.id,
                                        blockViewModel,
                                        block.rightBlock,
                                        false
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

@Composable
fun OutputConsole(blockViewModel: CodeBlockViewModel) {
    val output by blockViewModel.output.collectAsState()

    Box(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "_______________",
            color = Color.White,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(LocalConfiguration.current.screenHeightDp.dp * 3 / 8)
            .background(Color.Black, shape = RectangleShape)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            itemsIndexed(output.result) { i, str ->
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    items(1) {
                        val isError =
                            output.errorline != -1 && i != 0 && i != output.result.size - 1
                        val text = if (isError) "$str: ${output.errorline}" else str

                        Text(
                            text = text,
                            fontFamily = CodeFont,
                            color = if (isError) Color.Red else Color.White,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Instructions() {
    val instructions = stringArrayResource(id = R.array.instructions)

    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = R.string.bang),
            color = Color.Black,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(LocalConfiguration.current.screenHeightDp.dp * 3 / 8),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(count = 1) {
            instructions.forEach { instruction ->
                if (instruction == stringResource(id = R.string.instruction) || instruction == stringResource(
                        R.string.author
                    )
                ) {
                    Text(text = instruction, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                } else {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                        text = instruction,
                        fontSize = 16.sp
                    )
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