package com.example.firstmobile.views.draganddrop

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import com.example.firstmobile.viewmodels.CodeBlockViewModel

// функция, которая отрисовывает перемещаемый блок
// получает операцию блока, сам блок и modifier для обёртки блока
@Composable
fun DragTarget(
    modifier: Modifier = Modifier,
    i: Int = -1,
    operationToDrop: CodeBlock,
    viewModel: CodeBlockViewModel,
    content: @Composable (() -> Unit)
) {
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    
    // задаем начальное состояние с дефолтными значениями из класса
    val state = LocalDragTargetInfo.current
    
    Box(modifier = modifier
        .onGloballyPositioned {
            currentPosition = it.localToWindow(Offset.Zero)
        }
        .pointerInput(Unit) { // логика при перемещении блока
            detectDragGesturesAfterLongPress(
                onDragStart = {
                    
                    state.operationToDrop = operationToDrop
                    state.dragPosition = currentPosition + it
                    state.draggableComposable = content
                    state.draggableRow = i
                    state.isDragging = true
                }, onDrag = { change, dragAmount ->
                    change.consume()
                    state.dragOffset += dragAmount
                }, onDragEnd = {
                    state.dragPosition = Offset.Zero
                    state.dragOffset = Offset.Zero
                    state.isDragging = false
                    
                    state.draggableRow = -1
                }, onDragCancel = {
                    state.dragPosition = Offset.Zero
                    state.dragOffset = Offset.Zero
                    state.isDragging = false
                    
                    state.draggableRow = -1
                })
        }) { // отображаем блок, который перетаскиваем
        content()
    }
}