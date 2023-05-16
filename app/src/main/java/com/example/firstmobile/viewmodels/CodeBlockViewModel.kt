package com.example.firstmobile.viewmodels

import androidx.lifecycle.ViewModel
import com.example.firstmobile.model.Braces
import com.example.firstmobile.model.CodeBlockOperation
import com.example.firstmobile.model.CodeBlock
import com.example.firstmobile.model.Interpreter
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
        
        if (isLeftChild) {
            currentCodeBlock.leftBlock = targetCodeBlock
        } else {
            // сохраняю скобки родителя
            if (currentCodeBlock.operation == CodeBlockOperation.PRINT) {
                targetCodeBlock.leftBrace =
                    currentCodeBlock.rightBlock!!.leftBrace
                targetCodeBlock.rightBrace =
                    currentCodeBlock.rightBlock!!.rightBrace
            }
            
            currentCodeBlock.rightBlock = targetCodeBlock
        }
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
        
        // toggle скобок операции
        if (withBraces) {
            currentCodeBlock.leftBrace =
                if (currentCodeBlock.leftBrace != Braces.DEFAULT) Braces.DEFAULT else Braces.OPEN_PARENTHESES
            currentCodeBlock.rightBrace =
                if (currentCodeBlock.rightBrace != Braces.DEFAULT) Braces.DEFAULT else Braces.CLOSE_PARENTHESES
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
        if (parentBlock.operation == CodeBlockOperation.PRINT) {
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
        
        if (i == (_blocks.value.size - 1)) {
            _blocks.value.add(CodeBlock())
        }
    }
    
    private fun parseSingleBlock(block: CodeBlock?, _text: String): String {
        var text = _text
        if (block == null) return text
        
        text += if (block.leftBrace != Braces.DEFAULT) "${block.leftBrace.symbol} " else ""
        
        text = parseSingleBlock(block.leftBlock, text)
        
        val centerText =
            if (block.operation == CodeBlockOperation.INPUT) block.input else block.operation.symbol
        
        text += if (centerText != "") "$centerText " else centerText
        
        text = parseSingleBlock(block.rightBlock, text)
        
        text += if (block.rightBrace != Braces.DEFAULT) block.rightBrace.symbol else ""
        
        return text
    }
    
    private fun parser(): MutableList<String> {
        val strings = mutableListOf<String>()
        
        _blocks.value.forEach { block ->
            val newString = parseSingleBlock(block, "").trim()
            if (newString != "") {
                strings.add(newString)
            }
            
            if (block.operation == CodeBlockOperation.LOOP || block.operation == CodeBlockOperation.CONDITION) {
                strings[strings.size - 1] += ":"
                strings.add("begin")
            }
        }
        
        return strings
    }
    
    fun reset() {
        _test.value += 1
        
        _blocks.value = mutableListOf(CodeBlock())
    }
    
    fun execute() {
//        val strings = parser()
        val strings = arrayOf("a = [1;2;3;4;5]", "print ( a[0] + a[1] )").toMutableList()
        
//        val output = Interpreter().executor(strings)
        val output = Interpreter().executor(strings)
        
        _output.value = output
    }
}