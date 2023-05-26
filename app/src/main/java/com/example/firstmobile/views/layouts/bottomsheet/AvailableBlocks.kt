package com.example.firstmobile.views.layouts.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.firstmobile.R
import com.example.firstmobile.model.CodeBlock
import com.example.firstmobile.model.CodeBlockOperation
import com.example.firstmobile.ui.theme.TextColor
import com.example.firstmobile.ui.theme.roundBorder
import com.example.firstmobile.utils.flowlayouts.FlowRow
import com.example.firstmobile.viewmodels.CodeBlockViewModel
import com.example.firstmobile.views.draganddrop.DragTarget
import com.example.firstmobile.views.layouts.DropItemLayout

@Composable
fun AvailableBlocks(blockViewModel: CodeBlockViewModel) {
    val blockNames = stringArrayResource(id = R.array.block_names)
    
    TopBang(isLight = true)
    
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(LocalConfiguration.current.screenHeightDp.dp * 3 / 8)
            .background(color = MaterialTheme.colors.background)
            .padding(vertical = 16.dp, horizontal = 4.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(CodeBlockOperation.DEFAULT.blocksList()) { i, row ->
            FlowRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = stringResource(id = R.string.marker) + blockNames[i],
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.secondary
                    )
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
                                    .roundBorder(
                                        backColor = MaterialTheme.colors.primary,
                                        borderColor = MaterialTheme.colors.surface
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
                                    
                                    Text(
                                        text = operationText,
                                        style = MaterialTheme.typography.body1,
                                        color = TextColor
                                    )
                                    
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