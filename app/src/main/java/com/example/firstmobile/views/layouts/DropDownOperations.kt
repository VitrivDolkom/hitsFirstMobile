package com.example.firstmobile.views.layouts

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.firstmobile.R
import com.example.firstmobile.model.CodeBlockOperation
import com.example.firstmobile.ui.theme.MiddleSize
import com.example.firstmobile.ui.theme.NormalWidth
import com.example.firstmobile.ui.theme.roundThinBorder
import com.example.firstmobile.viewmodels.CodeBlockViewModel
import java.util.*

@Composable
fun DropdownDemo(
    i: Int,
    id: UUID,
    items: List<CodeBlockOperation>,
    operation: CodeBlockOperation,
    viewModel: CodeBlockViewModel
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .size(MiddleSize)
            .roundThinBorder(
                backColor = MaterialTheme.colors.background,
                borderColor = MaterialTheme.colors.primary
            ), contentAlignment = Alignment.Center
    ) {
        Text(
            text = operation.symbol,
            modifier = Modifier
                .offset(y = (-3).dp)
                .clickable(onClick = { expanded = true }),
            style = MaterialTheme.typography.subtitle2
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(color = MaterialTheme.colors.background)
                .border(1.dp, color = MaterialTheme.colors.secondary)
                .width(NormalWidth),
            offset = DpOffset(x = (-8).dp, y = 0.dp)
        ) {
            items.forEachIndexed { index, operation ->
                DropdownMenuItem(modifier = Modifier.fillMaxWidth(), onClick = {
                    if (items[index] == CodeBlockOperation.BRACES) {
                        viewModel.changeOperation(i, id, items[index], true)
                    } else {
                        viewModel.changeOperation(i, id, items[index])
                    }
                    
                    expanded = false
                }) {
                    if (operation == CodeBlockOperation.INPUT || operation == CodeBlockOperation.DEFAULT) {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = painterResource(id = R.drawable.trash),
                            contentDescription = stringResource(id = R.string.remove_description)
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = operation.symbol,
                                style = if (operation.isLogicOperation()) MaterialTheme.typography.subtitle1 else MaterialTheme.typography.subtitle2
                            )
                        }
                    }
                }
            }
        }
    }
}