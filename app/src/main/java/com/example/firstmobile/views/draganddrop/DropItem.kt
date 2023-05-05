package com.example.firstmobile.views.draganddrop

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import com.example.firstmobile.viewmodels.CodeBlockViewModel
import java.util.UUID

// место куда можно перетаскивать блоки
@Composable
fun DropItem(
    i: Int,
    id: UUID,
    isLeftChild: Boolean,
    modifier: Modifier = Modifier,
    blockViewModel: CodeBlockViewModel,
    content: @Composable (BoxScope.(isHovered: Boolean, isFullField: Boolean) -> Unit)
) {
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
    
    Box(modifier = modifier.onGloballyPositioned {
        it.boundsInWindow().let { rect ->
            isDropTarget = rect.contains(dragPosition + dragOffset)
            isDragLeaving = !isDropTarget && draggableRow == i && draggableId == id
        }
    }) {
        
        // блок пуст и в него перетащили новый блок
        if (isDropTarget && !isDragging) {
            isFullField = true
            blockViewModel.addBlock(operationToDrop, i, id, isLeftChild)
        }
        
        // пользователь перетащил содержание блока в другое место
        if (isDragLeaving && !isDragging) {
            isFullField = false
            blockViewModel.addBlock(null, i, id, isLeftChild)
        }
        
        content(isDropTarget, isFullField)
    }
}