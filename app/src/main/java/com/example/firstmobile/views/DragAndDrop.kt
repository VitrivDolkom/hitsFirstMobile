package com.example.firstmobile.views

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import com.example.firstmobile.model.MathOperation
import com.example.firstmobile.viewmodels.AddBlockViewModel

internal val LocalDragTargetInfo = compositionLocalOf { DragTargetInfo() }

// функция, которая отрисовывает перемещаемый блок
// получает операцию блока, сам блок и modifier для обёртки блока
@Composable
fun DragTarget(
    modifier: Modifier = Modifier,
    id: Int = -1,
    operationToDrop: MathOperation,
    viewModel: AddBlockViewModel,
    content: @Composable (() -> Unit)
) {
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    
    // задаем начальное состояние с дефолтными значениями из класса
    val state = LocalDragTargetInfo.current
    
    Box(modifier = modifier
        .onGloballyPositioned {
            currentPosition = it.localToWindow(Offset(-50f, -50f))
        }
        .pointerInput(Unit) { // логика при перемещении блока
            detectDragGesturesAfterLongPress(onDragStart = {
                viewModel.startDragging()
            
                state.isDragging = true
                state.operationToDrop = operationToDrop
                state.dragPosition = currentPosition + it
                state.draggableComposable = content
                state.draggableId = id
            }, onDrag = { change, dragAmount ->
                change.consume()
                state.dragOffset += dragAmount
            }, onDragEnd = {
                viewModel.stopDragging()
                state.dragPosition = Offset.Zero
                state.dragOffset = Offset.Zero
                state.isDragging = false
            }, onDragCancel = {
                viewModel.stopDragging()
                state.dragPosition = Offset.Zero
                state.dragOffset = Offset.Zero
                state.isDragging = false
            })
        }) { // отображаем блок, который перетаскиваем
        content()
    }
}

// место куда можно перетаскивать блоки
@Composable
fun DropItem(
    i: Int,
    j: Int,
    id: Int,
    modifier: Modifier = Modifier,
    blockViewModel: AddBlockViewModel,
    content: @Composable (BoxScope.(isHovered: Boolean, isFullField: Boolean, operation: MathOperation) -> Unit)
) {
    val dragInfo = LocalDragTargetInfo.current
    val isDragging = dragInfo.isDragging
    val dragPosition = dragInfo.dragPosition
    val dragOffset = dragInfo.dragOffset
    val operation = dragInfo.operationToDrop
    val draggableId = dragInfo.draggableId
    
    var isDropTarget by remember { mutableStateOf(false) }
    var isDragLeaving by remember { mutableStateOf(false) }
    var isBecomingEmpty by remember { mutableStateOf(false) }
    var isFullField by remember { mutableStateOf(false) }
    
    Box(modifier = modifier.onGloballyPositioned {
        it.boundsInWindow().let { rect ->
            isDropTarget = rect.contains(dragPosition + dragOffset + Offset(50f, 50f))
            isDragLeaving = !isDropTarget && draggableId == id
        }
    }) {
        if (isDropTarget && !isDragging && !isFullField) {
            isFullField = true
            blockViewModel.addBlock(operation, i, j)
        }
        
        isBecomingEmpty = isDragLeaving && !isDragging
        
        if (isBecomingEmpty) {
            isFullField = false
            blockViewModel.addBlock(MathOperation.DEFAULT, i, j)
        }
        
        val data = blockViewModel.getOperation(i, j)
        
        content(isDropTarget, isFullField, data)
    }
}

// класс с информацией о блоке, который перетаскивают
internal class DragTargetInfo {
    var isDragging by mutableStateOf(false)
    var dragPosition by mutableStateOf(Offset.Zero)
    var dragOffset by mutableStateOf(Offset.Zero)
    var draggableComposable by mutableStateOf<(@Composable () -> Unit)?>(null)
    var operationToDrop by mutableStateOf(MathOperation.DEFAULT)
    var draggableId by mutableStateOf(-1)
}

// экран, на котором можно перетаскивать draggable элементы

@Composable
fun DraggableScreen(
    modifier: Modifier = Modifier, content: @Composable () -> Unit
) {
    val state = remember {
        DragTargetInfo()
    }
    
    CompositionLocalProvider(
        LocalDragTargetInfo provides state
    ) {
        Box(
            modifier = modifier.fillMaxSize()
        ) {
            content()
            
            if (state.isDragging) {
                var targetSize by remember { mutableStateOf(IntSize.Zero) }
                
                Box(modifier = Modifier
                    .graphicsLayer { // рисуем перетаскиваемый блок
                        val offset =
                            state.dragPosition + state.dragOffset // делаем его чуть больше и определяем позицию
                        alpha = if (targetSize == IntSize.Zero) 0f else 1f
                        scaleX = 1.1f
                        scaleY = 1.1f
                        translationX = offset.x.minus(targetSize.width / 2)
                        translationY = offset.y.minus(targetSize.height / 2)
                    }
                    .onGloballyPositioned { // делаем размер равынй размеру элемента, который перетаскивают (globallyPositioned)
                        targetSize = it.size
                    }) {
                    state.draggableComposable?.invoke()
                }
            }
        }
    }
}