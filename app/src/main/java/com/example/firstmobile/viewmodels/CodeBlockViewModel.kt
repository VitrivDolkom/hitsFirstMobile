package com.example.firstmobile.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.firstmobile.views.draganddrop.CodeBlock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class CodeBlockViewModel : ViewModel() {
    
    private var _blocks = MutableStateFlow(mutableListOf(CodeBlock()))
    val blocks = _blocks.asStateFlow()
    
    private var _test = MutableStateFlow(0)
    val test = _test.asStateFlow()
    
    private fun dfs(currentCodeBlock: CodeBlock?, targetCodeBlock: CodeBlock, id: UUID, isLeftChild: Boolean) {
        if (currentCodeBlock == null) return
        
        if (currentCodeBlock.id != id) {
            dfs(currentCodeBlock.leftBlock, targetCodeBlock, id, isLeftChild)
            dfs(currentCodeBlock.rightBlock, targetCodeBlock, id, isLeftChild)
            return
        }
        
        if (isLeftChild) currentCodeBlock.leftBlock = targetCodeBlock
        else currentCodeBlock.rightBlock = targetCodeBlock
    }
    
    private fun isThereChildren(codeBlock: CodeBlock): Boolean {
        return codeBlock.leftBlock != null || codeBlock.rightBlock != null
    }
    
    fun addBlock(operation: CodeBlock?, i: Int, id: UUID, isLeftChild: Boolean) {
        _test.value += 1
        
        if (operation == null) {
            _blocks.value[i] = CodeBlock()
            return
        }
        
        if (_blocks.value[i].operation != "") {
            dfs(_blocks.value[i], operation, id, isLeftChild)
        } else {
            _blocks.value[i] = operation
        }
        
        if (i == (_blocks.value.size - 1)) {
            _blocks.value.add(CodeBlock())
        }
        
        Log.d("MyTag", "${_blocks.value}")
    }
}