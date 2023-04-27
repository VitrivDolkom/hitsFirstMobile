package com.example.firstmobile.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.firstmobile.model.MathOperation
import com.example.firstmobile.utils.flowlayouts.FlowRow
import kotlin.math.roundToInt

class CodeBlockActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            Column(
                modifier = Modifier.fillMaxSize()
            
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.6f)
                        .background(Color.Cyan)
                ) {
                    Text(text = "I am text")
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
                            modifier = Modifier.padding(top = 30.dp)
                        ) {
                            val mathOperation = enumValues<MathOperation>()
                
                            mathOperation.forEach {
                                CodeBlock(it)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CodeBlock(operation: MathOperation) {
    val isMovable = remember { mutableStateOf(true) }
    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }
    
    Box(modifier = Modifier
        .offset {
            IntOffset(x = offsetX.value.roundToInt(), y = offsetY.value.roundToInt())
        }
        .pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
                change.consumeAllChanges()
            
                if (isMovable.value) {
                    if ((offsetX.value + dragAmount.x) < 50) {
                        offsetX.value += dragAmount.x
                    } else {
                        isMovable.value = false
                    }
                    offsetY.value += dragAmount.y
                }
            }
        }
        .width(64.dp)
        .height(32.dp)
        .background(Color.Blue), contentAlignment = Alignment.Center) {
        Text(text = operation.symbol.toString())
    }
}