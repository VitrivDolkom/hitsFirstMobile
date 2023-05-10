package com.example.firstmobile.views.draganddrop

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import com.example.firstmobile.ui.theme.BlockShape
import com.example.firstmobile.viewmodels.CodeBlockViewModel
import java.util.UUID


@Composable
fun DropItem(
    i: Int,
    id: UUID,
    isLeftChild: Boolean,
    modifier: Modifier = Modifier,
    blockViewModel: CodeBlockViewModel,
    content: @Composable (BoxScope.(isHovered: Boolean, isFullField: Boolean) -> Unit)
) { // контэйнер, в который можно перетаскивать блоки
    val dragInfo = LocalDragTargetInfo.current
    
    val isDragging = dragInfo.isDragging
    val dragPosition = dragInfo.dragPosition
    val dragOffset = dragInfo.dragOffset
    val operationToDrop = dragInfo.operationToDrop
    val draggableRow = dragInfo.draggableRow
    val draggableId = dragInfo.draggableId
    
    var isDropTarget by remember { mutableStateOf(false) }
    var isDragLeaving by remember { mutableStateOf(false) }
    var isFullField by remember { mutableStateOf(false) }
    
    Box(modifier = modifier.background(color= Color.Green, shape = BlockShape)
        .onGloballyPositioned {
        it.boundsInWindow().let { rect ->
            isDropTarget = rect.contains(dragPosition + dragOffset)
            isDragLeaving = !isDropTarget && draggableRow == i && draggableId == id
        }
    }) {
        
        // блок пуст и в него перетащили новый блок
        if (isDropTarget && !isDragging && i != -1) {
            isFullField = true
            blockViewModel.addBlock(operationToDrop, i, id, isLeftChild)
        }
        
        // пользователь перетащил содержание блока в другое место
        if (isDragLeaving && !isDragging && i != -1) {
            isFullField = false
            blockViewModel.addBlock(null, i, id, isLeftChild)
        }
        
        // здесь можно менять например задний цвет или рамку блока
        // isDragLeaving - когда чел перетаскивает в другое место можно помечать красным например
        content(isDropTarget, isDragLeaving)
    }
}