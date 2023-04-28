package com.example.firstmobile.views

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.firstmobile.utils.flowlayouts.FlowRow

@Composable
fun MainScreen(
    blockViewModel: AddBlockViewModel
) {
    
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f)
                .verticalScroll(rememberScrollState())
        ) {
            blockViewModel.addedBlocks.forEachIndexed { i, mathOperations ->
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    mathOperations.forEachIndexed { j, operation ->
                        DropItem(
                            i,
                            j,
                            modifier = Modifier
                                .width(80.dp)
                                .height(40.dp)
                                .background(Color.White),
                            blockViewModel = blockViewModel
                        ) { isHovered, isFullField, operation ->
                            if (operation != MathOperation.DEFAULT) {
                                blockViewModel.addBlock(operation, i, j)
                                
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .border(
                                            1.dp, color = Color.Red, shape = RoundedCornerShape(15.dp)
                                        )
                                        .background(Color.Gray.copy(0.5f), RoundedCornerShape(15.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = operation.symbol.toString(), fontSize = 32.sp)
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .border(
                                            1.dp, color = Color.Red, shape = RoundedCornerShape(15.dp)
                                        )
                                        .background(Color.Gray.copy(0.5f), RoundedCornerShape(15.dp)),
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
        
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(count = 10) {
                FlowRow(
                    modifier = Modifier.padding(16.dp)
                ) {
                    blockViewModel.blocks.forEach { operation ->
                        DragTarget(
                            operationToDrop = operation, viewModel = blockViewModel
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
                                    text = operation.symbol.toString(),
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}