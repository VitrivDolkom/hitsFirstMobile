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
    
    fun updateInput(i: Int, id: UUID, newText: String, isLeftChild: Boolean) {
        _test.value += 1
        
        val updatedInputBlock = CodeBlock(null, CodeBlockOperation.INPUT, null, id, newText)
        appendNewChild(_blocks.value[i], updatedInputBlock, id, isLeftChild)
    }
    
    private fun appendNewChild(
        currentCodeBlock: CodeBlock?, targetCodeBlock: CodeBlock, id: UUID, isLeftChild: Boolean
    ) {
        if (currentCodeBlock == null) return
        
        if (currentCodeBlock.id != id) {
            appendNewChild(currentCodeBlock.leftBlock, targetCodeBlock, id, isLeftChild)
            appendNewChild(currentCodeBlock.rightBlock, targetCodeBlock, id, isLeftChild)
            return
        }
        
        if (isLeftChild) currentCodeBlock.leftBlock = targetCodeBlock
        else currentCodeBlock.rightBlock = targetCodeBlock
    }
    
    private fun appendNewOperation(currentCodeBlock: CodeBlock?, newOperation: CodeBlockOperation, id: UUID) {
        if (currentCodeBlock == null) return
        
        if (currentCodeBlock.id != id) {
            appendNewOperation(currentCodeBlock.leftBlock, newOperation, id)
            appendNewOperation(currentCodeBlock.rightBlock, newOperation, id)
            return
        }
        
        currentCodeBlock.operation = newOperation
    }
    
    fun changeOperation(i: Int, id: UUID, newOperation: CodeBlockOperation) {
        _test.value += 1
        appendNewOperation(_blocks.value[i], newOperation, id)
    }
    
    fun addBlock(parentBlock: CodeBlock?, i: Int, id: UUID, isLeftChild: Boolean) {
        _test.value += 1
        
        if (parentBlock == null) {
            _blocks.value[i] = CodeBlock()
            return
        }
        
        // по дефолту делаем детей инпутами
        val newLeftBlock = if (parentBlock.leftBlock == null) {
            CodeBlock(null, CodeBlockOperation.INPUT, null)
        } else parentBlock.leftBlock
        val newRightBlock = if (parentBlock.rightBlock == null) {
            CodeBlock(null, CodeBlockOperation.INPUT, null)
        } else parentBlock.rightBlock
    
        // создаю копию блока, чтобы сменить id
        val block = CodeBlock(
            newLeftBlock, parentBlock.operation, newRightBlock, UUID.randomUUID(), parentBlock.input
        )
        
        if (_blocks.value[i].operation != CodeBlockOperation.DEFAULT) {
            appendNewChild(_blocks.value[i], block, id, isLeftChild)
        } else {
            _blocks.value[i] = block
        }
        
        if (i == (_blocks.value.size - 1)) {
            _blocks.value.add(CodeBlock())
        }
    }
    
    private fun parseSingleBlock(block: CodeBlock?, _text: String): String {
        var text = _text
        if (block == null) return text
        
        text = parseSingleBlock(block.leftBlock, text)
        
        text += "${if (block.operation == CodeBlockOperation.INPUT) block.input else block.operation.symbol} "
        
        text = parseSingleBlock(block.rightBlock, text)
        
        return text
    }
    
    private fun parser(): MutableList<String> {
        val strings = mutableListOf<String>()
        
        _blocks.value.forEach { block ->
            strings.add(parseSingleBlock(block, ""))
            if (block.operation.isSpecialOperation()) strings.add("begin")
        }
        
        return strings
    }
    
    fun execute() {
        val strings = parser()
        
        // отдаю строки в интерпритатор и получаю output ...
        // val output = Interpreter.someFun(strings)
        
        _output.value = strings
    }
}