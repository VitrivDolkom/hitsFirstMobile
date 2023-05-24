package com.example.firstmobile.viewmodels

import androidx.lifecycle.ViewModel
import com.example.firstmobile.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.math.floor

class CodeBlockViewModel : ViewModel() {
    
    private var _blocks = MutableStateFlow(mutableListOf(CodeBlock()))
    val blocks = _blocks.asStateFlow()
    
    private var _output = MutableStateFlow(CodeResult(mutableListOf(), -1))
    val output = _output.asStateFlow()
    
    private var _changesNum = MutableStateFlow(0)
    val changesNum = _changesNum.asStateFlow()
    
    fun updateInput(
        i: Int,
        id: UUID,
        newText: String,
        isLeftChild: Boolean,
        leftBrace: Braces,
        rightBrace: Braces
    ) {
        _changesNum.value += 1
        
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
        _changesNum.value += 1
        appendNewOperation(_blocks.value[i], newOperation, id, withBraces)
    }
    
    fun addBlock(
        parentBlock: CodeBlock?, i: Int, id: UUID, isLeftChild: Boolean
    ) {
        _changesNum.value += 1
        
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
    
    fun shift(i: Int) {
        _changesNum.value += 1
        
        for (index in 0 until i) {
            _blocks.value[i] = _blocks.value[i + 1]
        }
    
//        for (index in i until _blocks.value.size) {
//            _blocks.value[i] = _blocks.value[i + 1]
//        }
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
            strings.add(newString)
            
            if (block.operation == CodeBlockOperation.LOOP || block.operation == CodeBlockOperation.CONDITION || block.operation == CodeBlockOperation.ELSE) {
                strings[strings.size - 1] += ":"
            }
            
            if (block.operation == CodeBlockOperation.LOOP || block.operation == CodeBlockOperation.CONDITION) {
                strings.add("begin")
            }
        }
        
        return strings
    }
    
    fun reset() {
        _changesNum.value += 1
        _blocks.value = mutableListOf(CodeBlock())
    }
    
    private fun getCurrentTime(): String {
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
        
        return formatter.format(time)
    }
    
    fun execute() {
        val strings = parser()
        
        val begin = System.nanoTime()
        val output = Interpreter().executor(strings)
        val end = System.nanoTime()
        
        val current = getCurrentTime()
        val executeTime = (end - begin) / 1e6
        
        output.result.add(0, "||-- ${current} --||")
        output.result.add("Execute time - $executeTime ms.")
        
        _output.value = output
    }
}