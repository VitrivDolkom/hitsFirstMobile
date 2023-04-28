package com.example.firstmobile.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.firstmobile.model.MathOperation

class AddBlockViewModel : ViewModel() {
    var isDragging by mutableStateOf(false)
        private set
    
    var blocks by mutableStateOf(enumValues<MathOperation>().toList())
        private set
    
    var addedBlocks = Array(11) { MutableList(blocks.count()) { MathOperation.DEFAULT } }
        private set
    
    fun startDragging() {
        isDragging = true
    }
    
    fun stopDragging() {
        isDragging = false
    }
    
    fun addBlock(operation: MathOperation, i: Int, j: Int) {
        addedBlocks[i][j] = operation
    }
    
    fun getOperation(i: Int, j: Int): MathOperation {
        return addedBlocks[i][j]
    }
}