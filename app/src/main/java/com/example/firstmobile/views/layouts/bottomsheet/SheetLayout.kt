package com.example.firstmobile.views.layouts.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firstmobile.R
import com.example.firstmobile.ui.theme.BigPadding
import com.example.firstmobile.ui.theme.BlueButton
import com.example.firstmobile.ui.theme.GreenButton
import com.example.firstmobile.ui.theme.RedButton
import com.example.firstmobile.viewmodels.CodeBlockViewModel

@Composable
fun SheetLayout(
    blockViewModel: CodeBlockViewModel, openSheet: (BottomSheetScreen) -> Unit
) {
    Row(
        Modifier
            .fillMaxSize()
            .padding(BigPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        FloatingActionButton(backgroundColor = BlueButton, onClick = {
            openSheet(BottomSheetScreen.Screen3)
        }) {
            Text(
                text = stringResource(id = R.string.question),
                style = MaterialTheme.typography.button,
                color = MaterialTheme.colors.background
            )
        }
        
        FloatingActionButton(backgroundColor = RedButton, onClick = {
            openSheet(BottomSheetScreen.Screen1)
        }) {
            Text(
                text = stringResource(id = R.string.blocks),
                style = MaterialTheme.typography.button,
                color = MaterialTheme.colors.background
            )
        }
        
        FloatingActionButton(backgroundColor = GreenButton, onClick = {
            blockViewModel.execute()
            openSheet(BottomSheetScreen.Screen2)
        }) {
            Text(
                text = stringResource(id = R.string.run),
                style = MaterialTheme.typography.button,
                color = MaterialTheme.colors.background
            )
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