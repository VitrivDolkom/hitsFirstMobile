package com.example.firstmobile.views.draganddrop

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import com.example.firstmobile.ui.theme.BlockShape
import com.example.firstmobile.ui.theme.DarkGreen
import com.example.firstmobile.viewmodels.CodeBlockViewModel
import java.util.*

// функция, которая отрисовывает перемещаемый блок
// получает операцию блока, сам блок и modifier для обёртки блока
@Composable
fun DragTarget(
    modifier: Modifier = Modifier,
    i: Int = -1,
    operationToDrop: CodeBlock,
    viewModel: CodeBlockViewModel,
    content: @Composable ( () -> Unit)
    ) {
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    val state = LocalDragTargetInfo.current

    Box(modifier = modifier
        .onGloballyPositioned {
            currentPosition = it.localToWindow(Offset.Zero)
        }
        .pointerInput(Unit) {
            // логика при перемещении блока
            detectDragGesturesAfterLongPress(onDragStart = {
                // записываем данные о блоке, который перетаскивает пользователь
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
        // отображаем блок, который перетаскиваем
        content()
    }
}