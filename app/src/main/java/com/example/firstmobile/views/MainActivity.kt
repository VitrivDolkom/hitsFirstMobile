package com.example.firstmobile.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firstmobile.ui.theme.BottomSheetShape
import com.example.firstmobile.utils.flowlayouts.FlowRow
import com.example.firstmobile.viewmodels.CodeBlockViewModel
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {
    
    private val blockViewModel = CodeBlockViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainLayout(blockViewModel)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MainLayout(blockViewModel: CodeBlockViewModel) {
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
        BottomSheetScaffold(
            sheetPeekHeight = 0.dp,
            scaffoldState = scaffoldState,
            sheetShape = BottomSheetShape,
            sheetContent = {
                currentBottomSheet?.let { currentSheet ->
                    SheetLayout(blockViewModel, currentSheet, closeSheet)
                }
            }) { paddingValues ->
            Box(Modifier.padding(paddingValues)) {
                MainScreen(blockViewModel, openSheet)
            }
        }
    }
}

@Composable
fun MainContent(blockViewModel: CodeBlockViewModel, openSheet: (BottomSheetScreen) -> Unit) {
    Row(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        
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
fun SheetLayout(blockViewModel: CodeBlockViewModel, currentScreen: BottomSheetScreen, onCloseBottomSheet: () -> Unit) {
    when (currentScreen) {
        BottomSheetScreen.Screen1 -> Screen1(blockViewModel)
        is BottomSheetScreen.Screen2 -> Screen2()
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
                                text = operation.symbol.toString(), fontSize = 32.sp, fontWeight = FontWeight.SemiBold
                            )
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

sealed class BottomSheetScreen() {
    object Screen1 : BottomSheetScreen()
    object Screen2 : BottomSheetScreen()
}