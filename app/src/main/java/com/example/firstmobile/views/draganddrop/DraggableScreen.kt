package com.example.firstmobile.views.draganddrop

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize

// экран, на котором можно перетаскивать draggable элементы
@Composable
fun DraggableScreen(
    modifier: Modifier = Modifier, content: @Composable () -> Unit
) {
    val state = remember { DragTargetInfo() }
    
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
                            state.dragPosition + state.dragOffset // определяем позицию
                        alpha = if (targetSize == IntSize.Zero) 0f else 1f
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