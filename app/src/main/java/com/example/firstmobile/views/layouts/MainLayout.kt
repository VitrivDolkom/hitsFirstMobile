package com.example.firstmobile.views.layouts

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.PowerManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getSystemService
import com.example.firstmobile.ui.theme.BottomSheetShape
import com.example.firstmobile.viewmodels.CodeBlockViewModel
import com.example.firstmobile.views.draganddrop.DraggableScreen
import kotlinx.coroutines.launch
import java.lang.Math.sqrt

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

@Composable
fun ShakeDetector() {
    var shakeCount by remember { mutableStateOf(0) }
    val sensorManager = LocalContext.current.getSystemService(SensorManager::class.java)

    DisposableEffect(sensorManager) {
        val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val sensorEventListener = object : SensorEventListener {
            private val SHAKE_THRESHOLD = 10f // Adjust this value to your desired sensitivity
            private var lastAcceleration = 0f
            private var lastUpdate = 0L

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]
                    val acceleration = kotlin.math.sqrt(x * x + y * y + z * z)

                    val currentTime = System.currentTimeMillis()
                    val timeDifference = currentTime - lastUpdate

                    if (timeDifference > 100) { // Add a delay between shakes to prevent multiple triggers
                        val deltaAcceleration = acceleration - lastAcceleration
                        lastAcceleration = acceleration
                        lastUpdate = currentTime

                        if (deltaAcceleration > SHAKE_THRESHOLD) {
                            shakeCount++
                        }
                    }
                }
            }
        }

        sensorManager.registerListener(sensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)

        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }

    TopAppBar(title = { Text("Shake Count: $shakeCount", fontSize = 20.sp) })
}