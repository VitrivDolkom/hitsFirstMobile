package com.example.firstmobile.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.firstmobile.model.CodeBlockOperation
import com.example.firstmobile.views.draganddrop.CodeBlock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class CodeBlockViewModel : ViewModel() {
    
    // блоки, перетащенные пользователем
    private var _blocks = MutableStateFlow(mutableListOf(CodeBlock()))
    val blocks = _blocks.asStateFlow()
    
    // результат выполненного кода
    private var _output = MutableStateFlow(mutableListOf(""))
    val output = _output.asStateFlow()
    
    private var _test = MutableStateFlow(0)
    val test = _test.asStateFlow()
    
    private fun appendNewChild(currentCodeBlock: CodeBlock?, targetCodeBlock: CodeBlock, id: UUID, isLeftChild: Boolean) {
        if (currentCodeBlock == null) return
        
        if (currentCodeBlock.id != id) {
            appendNewChild(currentCodeBlock.leftBlock, targetCodeBlock, id, isLeftChild)
            appendNewChild(currentCodeBlock.rightBlock, targetCodeBlock, id, isLeftChild)
            return
        }
        
        if (isLeftChild) currentCodeBlock.leftBlock = targetCodeBlock
        else currentCodeBlock.rightBlock = targetCodeBlock
    }
    
    fun addBlock(block: CodeBlock?, i: Int, id: UUID, isLeftChild: Boolean) {
        _test.value += 1
        
        if (block == null) {
            _blocks.value[i] = CodeBlock()
            return
        }
        
        if (_blocks.value[i].operation != CodeBlockOperation.DEFAULT) {
            appendNewChild(_blocks.value[i], block, id, isLeftChild)
        } else {
            _blocks.value[i] = block
        }
        
        if (i == (_blocks.value.size - 1)) {
            _blocks.value.add(CodeBlock())
        }
    }
    
    fun parseSingleBlock(block: CodeBlock?, _text: String): String {
        var text = _text
        if (block == null) return text
        
        text = parseSingleBlock(block.leftBlock, text)
        
        text += "${block.operation.symbol} "
        
        text = parseSingleBlock(block.rightBlock, text)
        
        return text
    }
    
    private fun parser(): Array<String> {
        val strings = Array(_blocks.value.size) { "n = $it" }
    
        _blocks.value.forEachIndexed { index, block ->
            strings[index] = parseSingleBlock(block, "")
        }
        
        return strings
    }
    
    fun execute() {
        val strings = parser()
        
        // отдаю строки в интерпритатор и получаю output ...
        // val output = Interpreter.someFun(strings)
        
        _output.value = strings.toMutableList()
    }
}