package com.example.firstmobile.views.draganddrop

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset

internal val LocalDragTargetInfo = compositionLocalOf { DragTargetInfo() }

// класс с информацией о блоке, который перетаскивают
internal class DragTargetInfo {
    var isDragging by mutableStateOf(false)
    var dragPosition by mutableStateOf(Offset.Zero)
    var dragOffset by mutableStateOf(Offset.Zero)
    var draggableComposable by mutableStateOf<(@Composable () -> Unit)?>(null)
    var operationToDrop by mutableStateOf<CodeBlock?>(null)
    var draggableRow by mutableStateOf(-1)
}
