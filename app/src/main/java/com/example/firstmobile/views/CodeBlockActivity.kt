package com.example.firstmobile.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
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
import com.example.firstmobile.ui.theme.FirstMobileTheme
import com.example.firstmobile.utils.flowlayouts.FlowRow
import com.example.firstmobile.viewmodels.AddBlockViewModel
import kotlin.math.roundToInt

class CodeBlockActivity : ComponentActivity() {
    private val blockViewModel = AddBlockViewModel()
            FirstMobileTheme {
                DraggableScreen(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    MainScreen(blockViewModel = blockViewModel)
                }
            }
        }
    }
}
