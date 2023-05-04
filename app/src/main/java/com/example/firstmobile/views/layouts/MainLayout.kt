package com.example.firstmobile.views.layouts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.firstmobile.ui.theme.BottomSheetShape
import com.example.firstmobile.viewmodels.CodeBlockViewModel
import com.example.firstmobile.views.draganddrop.DraggableScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainLayout(blockViewModel: CodeBlockViewModel) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()
    
    var currentBottomSheet: BottomSheetScreen? by remember {
        mutableStateOf(BottomSheetScreen.Screen1)
    }
    
    val closeSheet: () -> Unit = {
        scope.launch {
            scaffoldState.bottomSheetState.collapse()
        }
    }
    
    val openSheet: (BottomSheetScreen) -> Unit = {
        scope.launch {
            currentBottomSheet = it
            scaffoldState.bottomSheetState.expand()
        }
    }
    
    DraggableScreen(
        modifier = Modifier.fillMaxSize()
    ) {
        BottomSheetScaffold(sheetPeekHeight = 0.dp,
            scaffoldState = scaffoldState,
            sheetShape = BottomSheetShape,
            sheetContent = {
                currentBottomSheet?.let { currentSheet ->
                    DifferentBottomSheets(blockViewModel, currentSheet, closeSheet)
                }
            }) { paddingValues ->
            Box(Modifier.padding(paddingValues)) {
                MainScreen(blockViewModel, openSheet)
            }
        }
    }
}