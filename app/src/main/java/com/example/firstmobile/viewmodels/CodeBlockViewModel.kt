package com.example.firstmobile.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.firstmobile.model.CodeBlockOperation

class CodeBlockViewModel : ViewModel() {
    var isDragging by mutableStateOf(false)
        private set
    
    var maxRow by mutableStateOf(1)
        private set
    
    var maxColumn by mutableStateOf(1)
        private set
    
    var addedBlocks = MutableList(20) { MutableList(20) { CodeBlockOperation.DEFAULT } }
        private set
    
    fun startDragging() {
        isDragging = true
    }
    
    fun stopDragging() {
        isDragging = false
    }
    
    
    fun addBlock(operation: CodeBlockOperation, i: Int, j: Int) {
        if (j == maxColumn) {
            maxColumn++
        }
        
        if (i == maxRow) {
            maxRow++
        }
    
        addedBlocks[i][j] = operation
    
    }
    
    fun getOperation(i: Int, j: Int): CodeBlockOperation {
        return addedBlocks[i][j]
    }
    
    fun shift(operation: CodeBlockOperation, i: Int, j: Int) {
        if (j == maxColumn) {
            maxColumn += 2
        }
    
        if (i == maxRow) {
            maxRow++
        }
    
        addedBlocks[i].add(j, operation)
    }
    
    fun reverseShift(i: Int, j: Int) {
        addedBlocks[i].forEachIndexed { column, _ ->
            if (column >= j && column < (addedBlocks[i].size - 1)) {
                addedBlocks[i][column] = addedBlocks[i][column + 1]
            }
        }
    }
}