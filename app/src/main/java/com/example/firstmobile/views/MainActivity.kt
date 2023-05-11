package com.example.firstmobile.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import com.example.firstmobile.viewmodels.CodeBlockViewModel
import com.example.firstmobile.views.layouts.MainLayout
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.example.firstmobile.views.layouts.ShakeDetector

@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {
    
    private val blockViewModel = CodeBlockViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShakeDetector()
            MainLayout(blockViewModel)
        }
    }
}
