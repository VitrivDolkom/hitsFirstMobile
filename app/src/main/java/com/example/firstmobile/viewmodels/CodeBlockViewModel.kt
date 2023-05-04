package com.example.firstmobile.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.firstmobile.views.draganddrop.CodeBlock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class CodeBlockViewModel : ViewModel() {
    
    private var _blocks = MutableStateFlow(mutableListOf(CodeBlock()))
    val blocks = _blocks.asStateFlow()
    
    private var _test = MutableStateFlow(0)
    val test = _test.asStateFlow()
    
    fun addBlock(operation: CodeBlock?, i: Int) {
        _test.value += 1
        
        if (operation != null) {
            _blocks.value[i] = operation
            if (i == (_blocks.value.size - 1)) {
                _blocks.value.add(CodeBlock())
            }
        } else {
            _blocks.value[i] = CodeBlock()
        }
        
        Log.d("MyTag", "${_blocks.value}")
    }
}