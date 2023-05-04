package com.example.firstmobile.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.firstmobile.model.CodeBlockOperation
import com.example.firstmobile.views.CodeBlock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class CodeBlockViewModel : ViewModel() {
    
    private val _blocks = MutableStateFlow(mutableListOf(CodeBlock()))
    val blocks = _blocks.asStateFlow()
    
    
    fun addBlock(operation: CodeBlock?, i: Int) {
//        if (i < _blocks.value.size) {
//
//        }
        
        
        if (operation != null) {
            _blocks.value[i] = operation
            _blocks.value.add(CodeBlock())
        } else {
            _blocks.value[i] = CodeBlock()
        }
    }
}