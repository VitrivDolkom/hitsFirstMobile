package com.example.firstmobile.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.firstmobile.model.MathOperation

class CodeBlockViewModel : ViewModel() {
    var isDragging by mutableStateOf(false)
        private set
    
    var blocks by mutableStateOf(enumValues<MathOperation>().toList())
        private set

    var addedBlocks = Array(11) { MutableList(blocks.count() * 2) { MathOperation.DEFAULT } }
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
    
    fun shift(operation: MathOperation, i: Int, j: Int) {
        
        addedBlocks[i].forEachIndexed { column, _ ->
            if ((addedBlocks[i].size - column - 1) > j) {
                addedBlocks[i][addedBlocks[i].size - column - 1] = addedBlocks[i][addedBlocks[i].size - column - 2]
            }
        }
    
        addedBlocks[i][j] = operation
    
        Log.d("MyTag", "сдвиг вперед- ${addedBlocks[i]}")
    }
    
    fun reverseShift(i: Int, j: Int) {
        addedBlocks[i].forEachIndexed { column, _ ->
            if (column > j && column < (addedBlocks[i].size - 1)) {
                addedBlocks[i][column] = addedBlocks[i][column + 1]
            }
        }
    
        Log.d("MyTag", "сдвиг назад - ${addedBlocks[i]}")
    }
}