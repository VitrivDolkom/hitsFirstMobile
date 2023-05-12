package com.example.firstmobile.viewmodels

import androidx.lifecycle.ViewModel
import com.example.firstmobile.model.Braces
import com.example.firstmobile.model.CodeBlockOperation
import com.example.firstmobile.model.CodeBlock
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
    
    fun updateInput(
        i: Int,
        id: UUID,
        newText: String,
        isLeftChild: Boolean,
        leftBrace: Braces,
        rightBrace: Braces
    ) {
        _test.value += 1
        
        val updatedInputBlock = CodeBlock(
            null,
            CodeBlockOperation.INPUT,
            null,
            id,
            newText,
            leftBrace,
            rightBrace
        )
        
        appendNewChild(_blocks.value[i], updatedInputBlock, id, isLeftChild)
    }
    
    private fun appendNewChild(
        currentCodeBlock: CodeBlock?,
        targetCodeBlock: CodeBlock,
        id: UUID,
        isLeftChild: Boolean
    ) {
        if (currentCodeBlock == null) return
        
        if (currentCodeBlock.id != id) {
            appendNewChild(
                currentCodeBlock.leftBlock, targetCodeBlock, id, isLeftChild
            )
            appendNewChild(
                currentCodeBlock.rightBlock, targetCodeBlock, id, isLeftChild
            )
            return
        }
        
        if (isLeftChild) currentCodeBlock.leftBlock = targetCodeBlock
        else currentCodeBlock.rightBlock = targetCodeBlock
    }
    
    private fun appendNewOperation(
        currentCodeBlock: CodeBlock?,
        newOperation: CodeBlockOperation,
        id: UUID,
        withBraces: Boolean = false
    ) {
        if (currentCodeBlock == null) return
        
        if (currentCodeBlock.id != id) {
            appendNewOperation(
                currentCodeBlock.leftBlock, newOperation, id, withBraces
            )
            appendNewOperation(
                currentCodeBlock.rightBlock, newOperation, id, withBraces
            )
            return
        }
        
        val leftChild = currentCodeBlock.leftBlock
        val rightChild = currentCodeBlock.rightBlock
        
        if (withBraces) {
            leftChild?.leftBrace =
                if (leftChild?.leftBrace != Braces.DEFAULT) Braces.DEFAULT else Braces.OPEN_PARENTHESES
            rightChild?.rightBrace =
                if (rightChild?.rightBrace != Braces.DEFAULT) Braces.DEFAULT else Braces.CLOSE_PARENTHESES
            return
        }
        
        currentCodeBlock.operation = newOperation
    }
    
    fun changeOperation(
        i: Int,
        id: UUID,
        newOperation: CodeBlockOperation,
        withBraces: Boolean = false
    ) {
        _test.value += 1
        appendNewOperation(_blocks.value[i], newOperation, id, withBraces)
    }
    
    fun addBlock(
        parentBlock: CodeBlock?, i: Int, id: UUID, isLeftChild: Boolean
    ) {
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
        
        // если массив, то добавляем [ ]
        if (parentBlock.operation == CodeBlockOperation.ARRAY_EQUAL) {
            newRightBlock?.leftBrace = Braces.OPEN_SQUARE
            newRightBlock?.rightBrace = Braces.CLOSE_SQUARE
        }
        
        // если специальные операции, добавляем ( )
        if (parentBlock.operation.isSpecialOperation()) {
            newRightBlock?.leftBrace = Braces.OPEN_PARENTHESES
            newRightBlock?.rightBrace = Braces.CLOSE_PARENTHESES
        }
        
        // создаю копию блока, чтобы сменить id
        val block = CodeBlock(
            newLeftBlock,
            parentBlock.operation,
            newRightBlock,
            UUID.randomUUID(),
            parentBlock.input,
            parentBlock.leftBrace,
            parentBlock.rightBrace
        )
        
        if (_blocks.value[i].operation != CodeBlockOperation.DEFAULT) {
            appendNewChild(_blocks.value[i], block, id, isLeftChild)
        } else {
            _blocks.value[i] = block
        }
        
        // добавляю еще один пустой блок
        if (i == (_blocks.value.size - 1)) {
            _blocks.value.add(CodeBlock())
        }
    }
    
    private fun parseSingleBlock(block: CodeBlock?, _text: String): String {
        var text = _text
        if (block == null) return text
        
        text = parseSingleBlock(block.leftBlock, text)
        
        val newText =
            "${block.leftBrace.symbol}${if (block.operation == CodeBlockOperation.INPUT) block.input else block.operation.symbol}${block.rightBrace.symbol}"
        
        text += newText
        
        text = parseSingleBlock(block.rightBlock, text)
        
        return text
    }
    
    private fun parser(): MutableList<String> {
        val strings = mutableListOf<String>()
        
        _blocks.value.forEach { block ->
            strings.add(parseSingleBlock(block, ""))
            if (block.operation == CodeBlockOperation.LOOP || block.operation == CodeBlockOperation.CONDITION) strings.add(
                "begin"
            )
        }
        
        return strings
    }
    
    fun reset() {
        _test.value += 1
        
        _blocks.value = mutableListOf(CodeBlock())
    }
    
    fun execute() {
        val strings = parser()
        
        // отдаю строки в интерпритатор и получаю output ...
//         val output = Interpreter.someFun(strings)
        
        _output.value = strings
    }
}