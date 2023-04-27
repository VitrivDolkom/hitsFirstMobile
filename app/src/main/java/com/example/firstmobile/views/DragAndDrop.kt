package com.example.firstmobile.views

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned

internal val LocalDragTargetInfo = compositionLocalOf { DragTargetInfo() }

// todo - функция, которая отрисовывает перемещаемый блок
// todo - получает операцию блока, сам блок и modifier для обёртки блока
@Composable
fun <T> DragTarget(
    modifier: Modifier = Modifier, operationToDrop: T, block: @Composable (() -> Unit)
) {
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    
    // todo - задаем начальное состояние с дефолтными значениями из класса
    val state = LocalDragTargetInfo.current
    
    Box(modifier = modifier
        .onGloballyPositioned {
            currentPosition = it.localToWindow(Offset.Zero)
        }
        .pointerInput(Unit) { // todo - логика при перемещении блока
            detectDragGesturesAfterLongPress(onDragStart = { // viewmodel start dragging logic
                state.isDragging = true
                state.operationToDrop = operationToDrop
                state.dragPosition = currentPosition + it
                state.draggableComposable = block
            }, onDrag = { change, dragAmount ->
                change.consumeAllChanges()
            
                state.dragOffset += dragAmount
            }, onDragEnd = { // viewmodel stop dragging logic
                state.dragOffset = Offset.Zero
                state.isDragging = false
            }, onDragCancel = { // viewmodel stop dragging logic
                state.dragOffset = Offset.Zero
                state.isDragging = false
            })
        }) { // todo - отображаем блок, который перетаскиваем
        block()
    }
}

// todo - место куда можно перетаскивать блоки
@Composable
fun <T> DropItem(
    modifier: Modifier, content: @Composable (BoxScope.(isHovered: Boolean, block: T?) -> Unit)
) {
    val dragInfo = LocalDragTargetInfo.current
    val isDragging = dragInfo.isDragging
    val dragPosition = dragInfo.dragPosition
    val dragOffset = dragInfo.dragOffset
    val operation = dragInfo.operationToDrop
    var isDropTarget by remember { mutableStateOf(false) }
    
    Box(modifier = modifier.onGloballyPositioned {
        it.boundsInWindow().let { rect ->
            isDropTarget = rect.contains(dragPosition + dragOffset)
        }
    }) { // todo - viewmovel logic for hover on drop target
        val data = if (isDropTarget && !isDragging) operation as T else null
        content(isDropTarget, data)
    }
}

// todo - класс с информацией о блоке, который перетаскивают
internal class DragTargetInfo {
    var isDragging by mutableStateOf(false)
    var dragPosition by mutableStateOf(Offset.Zero)
    var dragOffset by mutableStateOf(Offset.Zero)
    var draggableComposable by mutableStateOf<(@Composable () -> Unit)?>(null)
    var operationToDrop by mutableStateOf<Any?>(null)
}

// todo - экран, на котором можно перетаскивать draggable элементы