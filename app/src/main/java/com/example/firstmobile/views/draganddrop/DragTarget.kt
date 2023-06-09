package com.example.firstmobile.views.draganddrop

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import com.example.firstmobile.model.CodeBlock
import java.util.*

@Composable
fun DragTarget(
    modifier: Modifier = Modifier,
    i: Int = -1,
    operationToDrop: CodeBlock,
    content: @Composable (() -> Unit)
) {
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    val state = LocalDragTargetInfo.current
    
    Box(modifier = modifier
        .onGloballyPositioned {
            currentPosition = it.localToWindow(Offset.Zero)
        }
        .pointerInput(Unit) {
            detectDragGesturesAfterLongPress(onDragStart = {
                state.operationToDrop = operationToDrop
                state.dragPosition = currentPosition + it
                state.draggableComposable = content
                state.isDragging = true
                state.draggableRow = i
                state.draggableId = operationToDrop.id
            
            }, onDrag = { change, dragAmount ->
                change.consume()
                state.dragOffset += dragAmount
            
            }, onDragEnd = {
                state.dragPosition = Offset.Zero
                state.dragOffset = Offset.Zero
                state.isDragging = false
                state.draggableRow = -1
                state.draggableId = UUID.randomUUID()
            
            }, onDragCancel = {
                state.dragPosition = Offset.Zero
                state.dragOffset = Offset.Zero
                state.isDragging = false
                state.draggableRow = -1
                state.draggableId = UUID.randomUUID()
            })
        }) {
        content()
    }
}