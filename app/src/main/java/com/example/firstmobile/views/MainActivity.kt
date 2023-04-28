package com.example.firstmobile.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firstmobile.ui.theme.BottomSheetShape
import kotlinx.coroutines.launch


@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainLayout()
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MainLayout() {
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

    BottomSheetScaffold(sheetPeekHeight = 0.dp, scaffoldState = scaffoldState,
        sheetShape = BottomSheetShape,
        sheetContent = {
            currentBottomSheet?.let { currentSheet ->
                SheetLayout(currentSheet, closeSheet)
            }
        }) { paddingValues ->
        Box(Modifier.padding(paddingValues)) {
            MainContent(openSheet)
        }
    }
}

@Composable
fun MainContent(openSheet: (BottomSheetScreen) -> Unit) {
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
                text = "+",
                fontSize = 30.sp
            )  // Процент видимости: ${sheetState.progress.fraction}
        }

        FloatingActionButton(onClick = {
            openSheet(BottomSheetScreen.Screen2)
        }) {
            Text(
                text = ">",
                fontSize = 30.sp
            )  // Процент видимости: ${sheetState.progress.fraction}
        }
    }
}

@Composable
fun SheetLayout(currentScreen: BottomSheetScreen, onCloseBottomSheet: () -> Unit) {
    when (currentScreen) {
        BottomSheetScreen.Screen1 -> Screen1()
        is BottomSheetScreen.Screen2 -> Screen2()
    }
}

@Composable
fun Screen1() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(Color.Green, shape = RectangleShape)
    ) {
        Text(
            text = "Блоки",
            Modifier
                .align(Alignment.Center)
                .padding(16.dp), color = Color.Black,
            fontSize = 15.sp
        )
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
                .padding(16.dp), color = Color.White, fontSize = 15.sp
        )
    }
}

sealed class BottomSheetScreen() {
    object Screen1 : BottomSheetScreen()
    object Screen2 : BottomSheetScreen()
}