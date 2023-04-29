package com.example.firstmobile.views

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.example.firstmobile.viewmodels.AddBlockViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firstmobile.model.MathOperation

@Composable
fun MainScreen(
    blockViewModel: AddBlockViewModel, openSheet: (BottomSheetScreen) -> Unit
) {
    MainContent(blockViewModel = blockViewModel, openSheet = openSheet)
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp)
                .verticalScroll(rememberScrollState())
        ) {
            blockViewModel.addedBlocks.forEachIndexed { i, mathOperations ->
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    mathOperations.forEachIndexed { j, _ ->
                        DropItem(
                            i = i,
                            j = j,
                            id = i * blockViewModel.addedBlocks.size + j,
                            modifier = Modifier
                                .width(80.dp)
                                .height(40.dp)
                                .background(Color.White),
                            blockViewModel = blockViewModel
                        ) { isHovered, _, operation ->
                            if (operation != MathOperation.DEFAULT) {
                                blockViewModel.addBlock(operation, i, j)
    
                                DragTarget(
                                    id = i * blockViewModel.addedBlocks.size + j, operationToDrop = operation, viewModel = blockViewModel
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(80.dp)
                                            .border(
                                                1.dp, color = Color.Red, shape = RoundedCornerShape(15.dp)
                                            )
                                            .background(Color.Gray.copy(0.5f), RoundedCornerShape(15.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = operation.symbol.toString(), fontSize = 32.sp, fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .border(
                                            1.dp,
                                            color = Color.Red,
                                            shape = RoundedCornerShape(15.dp)
                                        )
                                        .background(
                                            Color.Gray.copy(0.5f),
                                            RoundedCornerShape(15.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isHovered) "Add here" else operation.symbol.toString(),
                                        style = MaterialTheme.typography.body1,
                                        color = Color.Black
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