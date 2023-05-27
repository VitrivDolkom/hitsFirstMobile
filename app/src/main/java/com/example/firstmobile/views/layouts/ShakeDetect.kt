package com.example.firstmobile.views.layouts

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import com.example.firstmobile.R
import com.example.firstmobile.viewmodels.CodeBlockViewModel
import kotlin.math.sqrt

@Composable
fun ShakeDetector(context: Context, blockViewModel: CodeBlockViewModel) {
    val sensorManager =
        LocalContext.current.getSystemService(SensorManager::class.java)
    
    DisposableEffect(sensorManager) {
        val accelerometerSensor =
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val sensorEventListener = object : SensorEventListener {
            private val SHAKE_THRESHOLD = 12f
            private val WEAK_SHAKE_THRESHOLD = 8f
            private var lastAcceleration = 0f
            private var lastUpdate = 0L
            
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]
                    val acceleration = sqrt(x * x + y * y + z * z)
                    
                    val currentTime = System.currentTimeMillis()
                    val timeDifference = currentTime - lastUpdate
                    
                    if (timeDifference > 350) {
                        val deltaAcceleration = acceleration - lastAcceleration
                        
                        if (deltaAcceleration > SHAKE_THRESHOLD) {
                            blockViewModel.reset()
                        } else if (deltaAcceleration > WEAK_SHAKE_THRESHOLD && lastAcceleration != 0f) {
                            Toast.makeText(context, R.string.weak_shake_text, Toast.LENGTH_SHORT).show()
                        }
                        
                        lastAcceleration = acceleration
                        lastUpdate = currentTime
                    }
                }
            }
        }
        
        sensorManager.registerListener(
            sensorEventListener,
            accelerometerSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        
        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }
}