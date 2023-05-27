package com.example.firstmobile.model

import java.util.*
import java.util.regex.Pattern
import kotlin.collections.*
import kotlin.math.floor

data class CodeResult(var result: MutableList<String>, var errorline: Int)

class Interpreter {
    private var arrays =
        mutableMapOf<String, Pair<MutableList<String>, MutableList<String>>>()
    
    private fun processArray(line: String) {
        var array = ""
        var name = ""
        
        if (CodeBlockOperation.EQUAL.symbol in line) {
            name =
                line.substring(0, line.indexOf(CodeBlockOperation.EQUAL.symbol))
                    .trim()
            array =
                line.substring(line.indexOf(CodeBlockOperation.EQUAL.symbol) + 1)
                    .trim()
        }
        
        array = array.substring(1, array.length - 1)
        
        val res = mutableListOf<Double>()
        
        array = array.replace(";", " ").replace(',', '.')
            .replace("\\s+".toRegex(), " ")
        
        for (element in array.split(" ")) {
            if (element != "") {
                res.add(element.toDouble())
            }
        }
        
        arrays[name] = Pair(
            res.map { it.toString() }.toMutableList(),
            List(res.size) { "$name[$it]" }.toMutableList()
        )
    }
    
    private fun polandCondition(line: String): Any { // Исходя из задуманной логики, может возвращаться разный тип данных
        val comparators = listOf(
            CodeBlockOperation.MORE.symbol,
            CodeBlockOperation.MORE_EQUAL.symbol,
            CodeBlockOperation.LESS_EQUAL.symbol,
            CodeBlockOperation.LESS.symbol,
            CodeBlockOperation.LESS_EQUAL.symbol,
            CodeBlockOperation.COMPARE_EQUAL.symbol,
            CodeBlockOperation.NOT_EQUAL.symbol,
            CodeBlockOperation.LOGIC_AND.symbol,
            CodeBlockOperation.LOGIC_OR.symbol,
            Braces.OPEN_PARENTHESES.symbol,
            Braces.CLOSE_PARENTHESES.symbol
        )
        
        var line =
            line.substring(line.indexOf(" ")).replace(" ", "").replace(":", "")
        
        for (element in comparators.reversed()) {
            line = line.replace(element, " $element ")
        }
        
        val temp = mutableListOf<String>()
        val lineList = line.split(" ")
        val temp2 = mutableListOf<String>()
        
        var i = 0
        while (i < lineList.size - 1) {
            if (lineList[i] + lineList[i + 1] in comparators) {
                temp2.add(lineList[i] + lineList[i + 1])
                i += 2
            } else {
                temp2.add(lineList[i])
                i += 1
            }
        }
        
        while (i < lineList.size) {
            temp2.add(lineList[i])
            i += 1
        }
        
        val newLine = temp2.toList()
        for (el in newLine) {
            if (el !in comparators && !ExtraSymbols.DOUBLE_PARENTHESES.symbol.contains(
                    el
                )
            ) {
                temp.add(
                    calculateExpression(el).replace(
                        ExtraSymbols.COMMA.symbol, ExtraSymbols.DOT.symbol
                    )
                )
            } else {
                temp.add(el)
            }
        }
        
        val comparatorsStack = mutableListOf<String>()
        val postfix = mutableListOf<String>()
        
        for (element in temp) {
            if (element !in comparators) {
                postfix.add(element)
            } else {
                if (element == Braces.OPEN_PARENTHESES.symbol) {
                    comparatorsStack.add(element)
                } else if (element == Braces.CLOSE_PARENTHESES.symbol) {
                    while (comparatorsStack.last() != Braces.OPEN_PARENTHESES.symbol) {
                        postfix.add(comparatorsStack.removeAt(comparatorsStack.lastIndex))
                    }
                    if (comparatorsStack.last() == Braces.OPEN_PARENTHESES.symbol) {
                        comparatorsStack.removeAt(comparatorsStack.lastIndex)
                    }
                } else {
                    while (comparatorsStack.isNotEmpty() && comparators.indexOf(
                            comparatorsStack.last()
                        ) <= comparators.indexOf(
                            element
                        )
                    ) {
                        postfix.add(comparatorsStack.removeAt(comparatorsStack.lastIndex))
                    }
                    comparatorsStack.add(element)
                }
            }
        }
        
        while (comparatorsStack.isNotEmpty()) {
            postfix.add(comparatorsStack.removeAt(comparatorsStack.lastIndex))
        }
        
        val resultStack = mutableListOf<Any>()
        for (element in postfix) {
            if (element !in comparators) {
                resultStack.add(element)
            } else {
                val str1 = resultStack.removeLast().toString().replace(
                    ExtraSymbols.FALSE.symbol, ExtraSymbols.ZERO.symbol
                ).replace(ExtraSymbols.TRUE.symbol, ExtraSymbols.ONE.symbol)
                val str2 = resultStack.removeLast().toString().replace(
                    ExtraSymbols.FALSE.symbol, ExtraSymbols.ZERO.symbol
                ).replace(ExtraSymbols.TRUE.symbol, ExtraSymbols.ONE.symbol)
                
                when (element) {
                    CodeBlockOperation.MORE.symbol -> resultStack.add(
                        str2.toFloat() > str1.toFloat()
                    )
                    CodeBlockOperation.LESS.symbol -> resultStack.add(
                        str2.toFloat() < str1.toFloat()
                    )
                    CodeBlockOperation.LESS_EQUAL.symbol -> resultStack.add(
                        str2.toFloat() <= str1.toFloat()
                    )
                    CodeBlockOperation.MORE_EQUAL.symbol -> resultStack.add(
                        str2.toFloat() >= str1.toFloat()
                    )
                    CodeBlockOperation.COMPARE_EQUAL.symbol -> resultStack.add(
                        str2.toFloat() == str1.toFloat()
                    )
                    CodeBlockOperation.LOGIC_AND.symbol -> resultStack.add(
                        (str2.toFloat() != 0.toFloat()) && (str1.toFloat() != 0.toFloat())
                    )
                    CodeBlockOperation.LOGIC_OR.symbol -> resultStack.add(
                        (str2.toFloat() != 0.toFloat()) || (str1.toFloat() != 0.toFloat())
                    )
                    CodeBlockOperation.NOT_EQUAL.symbol -> resultStack.add(
                        str2.toFloat() != str1.toFloat()
                    )
                }
            }
        }
        
        return resultStack.last()
    }
    
    private fun conditionFinder(code: List<String>): MutableList<List<Any>> { // Используем Any, из-за сложной структуризации данных о циклах и условиях
        val temp = mutableListOf<String>()
        var flag = 0
        var counter = 0
        val conditions = mutableListOf<List<Any>>()
        val transitionsDictionary = mutableMapOf<Int, Any>()
        var laststartpoint = -1
        var branchChecker = -1
        
        for ((index, line) in code.withIndex()) {
            if (CodeBlockOperation.ELSE.symbol in line) {
                branchChecker = index + 1
            }
            
            if (CodeBlockOperation.BEGIN.symbol in line && CodeBlockOperation.CONDITION.symbol in code[index - 1]) {
                flag = 1
                counter += 1
                laststartpoint = index
                transitionsDictionary[index - 1] = index + 1
            }
            
            if (CodeBlockOperation.BLOCK_END.symbol in line && flag == 1) {
                flag = 1
                counter -= 1
                var k: Int
                
                if (code[index - 1] != CodeBlockOperation.BLOCK_END.symbol) {
                    k = 1
                    transitionsDictionary[index - 1] = index + k
                    
                    if (branchChecker == -1) {
                        transitionsDictionary[laststartpoint - 1] = listOf(
                            transitionsDictionary[laststartpoint - 1],
                            transitionsDictionary[index - 1]
                        )
                    } else {
                        transitionsDictionary[laststartpoint - 1] = listOf(
                            transitionsDictionary[laststartpoint - 1],
                            branchChecker
                        )
                        transitionsDictionary[branchChecker - 1] =
                            transitionsDictionary[index - 1] as Any
                    }
                }
            }
            
            if (flag == 1) {
                temp.add(line)
                if (index - 1 in transitionsDictionary.values && (index - 1 !in transitionsDictionary)) {
                    transitionsDictionary[index - 1] = index
                }
            }
            
            if (counter == 0 && temp.isNotEmpty()) {
                flag = 0
                
                if (transitionsDictionary[index - temp.size] == index + 1 || transitionsDictionary[index - temp.size] is Int) {
                    if (branchChecker == -1) {
                        transitionsDictionary[index - temp.size] = listOf(
                            transitionsDictionary[index - temp.size], index + 1
                        )
                    } else {
                        transitionsDictionary[index - temp.size] = listOf(
                            transitionsDictionary[index - temp.size],
                            branchChecker
                        )
                    }
                }
                
                if (CodeBlockOperation.CONDITION.symbol in code[index - temp.size]) {
                    val target =
                        if (branchChecker == -1) index + 1 else branchChecker
                    
                    conditions.add(
                        listOf(
                            listOf(index - temp.size, target),
                            transitionsDictionary.toMutableMap(),
                            listOf(code[index - temp.size]) + temp
                        )
                    )
                }
                
                transitionsDictionary.clear()
                temp.clear()
                branchChecker = -1
            }
        }
        
        return conditions
    }
    
    private fun cycleFinder(code: List<String>): MutableList<List<Any>> { // Используем Any, аналогично с поиском условий
        val temp = mutableListOf<String>()
        var flag = 0
        var counter = 0
        val loop = mutableListOf<List<Any>>()
        
        val transitionsDictionary =
            mutableMapOf<Int, Any>() // Тип значения может меняться (если обычная строка - Int, если while - то list
        
        var laststartpoint = -1
        val stack = mutableListOf<String>()
        
        for ((index, line) in code.withIndex()) {
            if (CodeBlockOperation.BEGIN.symbol in line && CodeBlockOperation.CONDITION.symbol in code[index - 1]) {
                stack.add(CodeBlockOperation.CONDITION.symbol)
            }
            
            if (CodeBlockOperation.BEGIN.symbol in line && CodeBlockOperation.LOOP.symbol in code[index - 1]) {
                stack.add(CodeBlockOperation.LOOP.symbol)
                
                flag = 1
                counter += 1
                laststartpoint = index
                transitionsDictionary[index - 1] = index + 1
            }
            if (CodeBlockOperation.BLOCK_END.symbol in line && flag == 1 && stack.removeAt(
                    stack.lastIndex
                ) == CodeBlockOperation.LOOP.symbol
            ) {
                flag = 1
                counter -= 1
                var k: Int
                
                if (code[index - 1] != CodeBlockOperation.BLOCK_END.symbol) {
                    k = 1
                    transitionsDictionary[index - 1] = index
                    
                    if (CodeBlockOperation.CONDITION.symbol !in code[laststartpoint - 1]) {
                        transitionsDictionary[index] = laststartpoint - 1
                    }
                    
                    if (transitionsDictionary[laststartpoint - 1] is Int) {
                        transitionsDictionary[laststartpoint - 1] = listOf(
                            transitionsDictionary[laststartpoint - 1], index + k
                        )
                    }
                }
            }
            
            if (flag == 1) {
                temp.add(line)
                
                if (index - 1 in transitionsDictionary.values && (index - 1 !in transitionsDictionary)) {
                    transitionsDictionary[index - 1] = index
                }
            }
            
            if (counter == 0 && temp.isNotEmpty()) {
                flag = 0
                
                if (transitionsDictionary[index - temp.size] == index + 1 || transitionsDictionary[index - temp.size] is Int) {
                    transitionsDictionary[index - temp.size] = listOf(
                        transitionsDictionary[index - temp.size], index + 1
                    )
                }
                
                transitionsDictionary[index] = index - temp.size
                if (CodeBlockOperation.LOOP.symbol in code[index - temp.size]) {
                    loop.add(
                        listOf(
                            listOf(index - temp.size, index + 1),
                            transitionsDictionary.toMutableMap(),
                            listOf(code[index - temp.size]) + temp
                        )
                    )
                }
                
                transitionsDictionary.clear()
                temp.clear()
            }
        }
        
        return loop
    }
    
    private fun showAnswer(obj: String): String {
        val pattern = Pattern.compile("(?<=\\(\\s).+(?=\\s\\))")
        val matcher = pattern.matcher(obj)
        
        if (matcher.find()) {
            val a = matcher.group()
            
            if (a in arrays) {
                return (arrays[a]?.first.toString())
            } else if (a in dictionary) {
                return (dictionary[a].toString())
            } else {
                return (calculateExpression(a))
            }
        }
        
        return ExecuteError.RUNTIME.symbol
    }
    
    private var dictionary =
        mutableMapOf<String, Any>() // Используем Any, т.к. по задуманной релизации в паре ключ-значение значение может быть либо Int, либо List
    
    private fun calculateExpression(expression: String): String {
        val expression = expression.replace(" ", "")
            .replace(ExtraSymbols.COMMA.symbol, ExtraSymbols.DOT.symbol)
        var variable = ""
        var answer = ""
        var arrayKey = ""
        var arrayValue = ""
        
        if (CodeBlockOperation.EQUAL.symbol in expression) {
            variable = expression.substring(
                0, expression.indexOf(CodeBlockOperation.EQUAL.symbol)
            )
            answer =
                expression.substring(expression.indexOf(CodeBlockOperation.EQUAL.symbol) + 1)
            if (Braces.OPEN_SQUARE.symbol in variable) {
                variable = variable.trim()
                val spltpos = variable.indexOf(Braces.OPEN_SQUARE.symbol)
                arrayKey = variable.substring(0, spltpos)
                arrayValue =
                    variable.substring(spltpos + 1, variable.length - 1)
            }
        } else {
            answer = expression.substring(0)
        }
        
        val splittedVector = Vector<String>()
        val temp: List<String>
        
        val postfix = Vector<String>()
        val operations =
            CodeBlockOperation.ADD.symbol + CodeBlockOperation.SUBTRACT.symbol + CodeBlockOperation.DIVIDE.symbol + CodeBlockOperation.MULTIPLY.symbol + ExtraSymbols.COMMA.symbol + CodeBlockOperation.PERCENT.symbol
        var globalResult = ""
        
        if (answer != "") {
            while (operations.contains(answer[answer.length - 1])) {
                answer = answer.substring(0, answer.length - 1)
            }
            
            val operations2 =
                CodeBlockOperation.ADD.symbol + CodeBlockOperation.DIVIDE.symbol + CodeBlockOperation.MULTIPLY.symbol + ExtraSymbols.DOUBLE_PARENTHESES.symbol + CodeBlockOperation.PERCENT.symbol
            
            for (elem in operations2) {
                answer = answer.replace(
                    elem.toString(), " $elem "
                )
            }
            
            for (element in Regex("(?<=\\[)[^\\[\\]]+(?=\\])").findAll(answer)) {
                if (element.value in dictionary) {
                    answer = answer.replace(
                        element.value, dictionary[element.value]!!.toString()
                    )
                }
            }
            
            for (element in Regex("[a-zA-Z]+\\[[^\\]]+\\]").findAll(answer)) {
                val key = element.value.substring(0, element.value.indexOf('['))
                
                var elementValue = element.value.substring(
                    element.value.indexOf('[') + 1, element.value.length - 1
                )
                
                elementValue =
                    calculateExpression(elementValue).toFloat().toString()
                
                
                answer = answer.replace(
                    element.value,
                    arrays[key]!!.first[elementValue.toDouble().toInt()]
                )
            }
            
            
            for ((key, value) in dictionary) {
                answer = answer.replace(key, value.toString())
            }
            
            temp = answer.split(' ')
            
            for (element in temp) {
                if (element.length >= 3) {
                    if (element[0] == '-') {
                        var it = 1
                        
                        val operations3 =
                            CodeBlockOperation.ADD.symbol + CodeBlockOperation.SUBTRACT.symbol + CodeBlockOperation.DIVIDE.symbol + CodeBlockOperation.MULTIPLY.symbol + CodeBlockOperation.PERCENT.symbol
                        
                        while (it < element.length && !operations3.contains(
                                element[it]
                            )
                        ) {
                            it += 1
                        }
                        
                        splittedVector.add(element.substring(0, it))
                        for (x in element.substring(it, element.length).replace(
                            CodeBlockOperation.SUBTRACT.symbol,
                            " ${CodeBlockOperation.SUBTRACT.symbol} "
                        ).split(' ')) {
                            
                            if (x != " " && x != "") {
                                splittedVector.add(x)
                            }
                        }
                    } else {
                        splittedVector.addAll(
                            element.replace(
                                CodeBlockOperation.SUBTRACT.symbol,
                                " ${CodeBlockOperation.SUBTRACT.symbol} "
                            ).split(' ')
                        )
                    }
                } else {
                    if (element.length == 2 && element[element.length - 1].toString() == CodeBlockOperation.SUBTRACT.symbol) {
                        splittedVector.add(element[0].toString())
                        splittedVector.add(element[element.length - 1].toString())
                    } else {
                        splittedVector.add(element)
                    }
                }
            }
            
            val comparatorsStack = Stack<String>()
            
            val splitted = splittedVector.filter { it.isNotEmpty() }
            for (expr in splitted) {
                val operations4 =
                    CodeBlockOperation.ADD.symbol + CodeBlockOperation.DIVIDE.symbol + CodeBlockOperation.SUBTRACT.symbol + CodeBlockOperation.MULTIPLY.symbol + ExtraSymbols.DOUBLE_PARENTHESES.symbol + CodeBlockOperation.PERCENT.symbol
                
                if (!operations4.contains(expr)) {
                    var newExpression: String = expr
                    
                    if (expr[expr.length - 1].toString() == ExtraSymbols.COMMA.symbol) {
                        newExpression = expr.substring(0, expr.length - 1)
                    }
                    
                    if (expr.contains(ExtraSymbols.COMMA.symbol)) {
                        newExpression = expr.replace(
                            ExtraSymbols.COMMA.symbol, ExtraSymbols.DOT.symbol
                        )
                    }
                    
                    postfix.add(newExpression)
                } else {
                    if (expr == Braces.OPEN_PARENTHESES.symbol) {
                        comparatorsStack.add(expr)
                    } else if (expr == Braces.CLOSE_PARENTHESES.symbol) {
                        while (comparatorsStack.last() != Braces.OPEN_PARENTHESES.symbol) {
                            postfix.add(
                                comparatorsStack.removeAt(
                                    comparatorsStack.lastIndex
                                )
                            )
                        }
                        if (comparatorsStack.last() == Braces.OPEN_PARENTHESES.symbol) {
                            comparatorsStack.removeAt(comparatorsStack.lastIndex)
                        }
                    } else {
                        val operations5 =
                            CodeBlockOperation.MULTIPLY.symbol + CodeBlockOperation.DIVIDE.symbol + CodeBlockOperation.PERCENT.symbol
                        val operations6 =
                            CodeBlockOperation.ADD.symbol + CodeBlockOperation.SUBTRACT.symbol
                        
                        while (!comparatorsStack.isEmpty() && (operations6.contains(
                                expr
                            ) && operations5.contains(
                                comparatorsStack.peek()
                            ) || operations6.contains(expr) && operations6.contains(
                                comparatorsStack.peek()
                            ) || operations5.contains(
                                expr
                            ) && operations5.contains(comparatorsStack.peek()))
                        ) {
                            postfix.add(comparatorsStack.pop())
                        }
                        
                        comparatorsStack.add(expr)
                    }
                }
            }
            while (!comparatorsStack.isEmpty()) {
                postfix.add(comparatorsStack.pop())
            }
            
            val flag = true
            val resultStack = Stack<String>()
            
            for (elem in postfix) {
                val operations7 =
                    CodeBlockOperation.ADD.symbol + CodeBlockOperation.SUBTRACT.symbol + CodeBlockOperation.MULTIPLY.symbol + CodeBlockOperation.DIVIDE.symbol + CodeBlockOperation.PERCENT.symbol
                
                if (!operations7.contains(elem)) {
                    if (elem == (CodeBlockOperation.SUBTRACT.symbol + ExtraSymbols.ZERO.symbol) || elem.toDouble() - 0.0 == 0.0) {
                        resultStack.add(ExtraSymbols.ZERO.symbol)
                    } else {
                        resultStack.add(elem)
                    }
                } else {
                    val firstOperand: String = resultStack.pop()
                    val secondOperand: String = resultStack.pop()
                    
                    if (elem == CodeBlockOperation.ADD.symbol) {
                        resultStack.push((firstOperand.toDouble() + secondOperand.toDouble()).toString())
                    }
                    if (elem == CodeBlockOperation.SUBTRACT.symbol) {
                        resultStack.push((secondOperand.toDouble() - firstOperand.toDouble()).toString())
                    }
                    if (elem == CodeBlockOperation.MULTIPLY.symbol) {
                        resultStack.push((firstOperand.toDouble() * secondOperand.toDouble()).toString())
                    }
                    if (elem == CodeBlockOperation.PERCENT.symbol) {
                        resultStack.push((secondOperand.toDouble() % firstOperand.toDouble()).toString())
                    }
                    if (elem == CodeBlockOperation.DIVIDE.symbol) {
                        resultStack.push(
                            floor(
                                secondOperand.toDouble()
                                    .div(firstOperand.toDouble())
                            ).toString()
                        )
                    }
                }
            }
            
            if (flag) {
                globalResult = resultStack.pop()
                    .replace(ExtraSymbols.COMMA.symbol, ExtraSymbols.DOT.symbol)
            }
            
            if (variable != "") {
                if (arrayValue != "") {
                    val newArrayValue =
                        calculateExpression(arrayValue).toFloat().toInt()
                    
                    if (!newArrayValue.toString()
                            .any { it.isLetter() } && "${arrayKey}[${newArrayValue}]" in arrays[arrayKey]!!.second
                    ) {
                        arrays[arrayKey]!!.first[newArrayValue] = globalResult
                    }
                } else {
                    dictionary[variable] = globalResult
                }
            }
        }
        
        return globalResult
    }
    
    fun executor(code: MutableList<String>): CodeResult {
        if (code.isNotEmpty() && code[code.size - 1] == "") {
            code.removeLast()
        }
        
        val ifs = conditionFinder(code)
        val cycles = cycleFinder(code)
        
        var iterator = 0
        
        val result = mutableListOf<String>()
        
        while (iterator < code.size) {
            try {
                val current = code[iterator].trim()
                
                if (ifs.isNotEmpty() && (((iterator >= (ifs[0][0] as List<Int>)[1]) && (cycles.isEmpty() || cycles.isNotEmpty() && (iterator >= (cycles[0][0] as List<Int>)[1]))) || cycles.isNotEmpty() && ((cycles[0][0] as List<Int>)[1] >= (ifs[0][0] as List<Int>)[1]) && iterator == (cycles[0][0] as List<Int>)[1])) {
                    ifs.removeAt(0)
                }
                if (cycles.isNotEmpty() && ((iterator >= (cycles[0][0] as List<Int>)[1]) || cycles.size >= 2 && (cycles[1][0] as List<Int>)[0] == iterator)) {
                    cycles.removeAt(0)
                }
                
                if (ifs.isNotEmpty() && CodeBlockOperation.CONDITION.symbol in current) {
                    val getResult = polandCondition(current)
                    
                    var indx = 0
                    while (indx < ifs.size && iterator != (ifs[indx][0] as List<Int>)[0]) {
                        indx += 1
                    }
                    if (indx == ifs.size) {
                        indx = 0
                    }
                    
                    if (getResult == true) {
                        iterator =
                            ((ifs[indx][1] as Map<Any, Any>)[iterator] as List<Int>)[0] as Int
                    } else {
                        iterator =
                            ((ifs[indx][1] as Map<Any, Any>)[iterator] as List<Int>)[1] as Int
                    }
                } else if (cycles.isNotEmpty() && CodeBlockOperation.LOOP.symbol in current) {
                    val getResult = polandCondition(current)
                    
                    if (getResult == true) {
                        iterator =
                            ((cycles[0][1] as Map<Any, Any>)[iterator] as List<Int>)[0]
                    } else {
                        iterator =
                            ((cycles[0][1] as Map<Any, Any>)[iterator] as List<Int>)[1]
                    }
                } else {
                    if (Regex("print\\s\\(.+\\)").matches(current)) {
                        result.add(showAnswer(current))
                    } else if (Regex("[a-zA-Z]+(\\s|)\\=(\\s|)\\[(\\s|)([a-zA-Z0-9\\.,]+(\\s|)\\;(\\s|))*[0-9]+(\\s|)\\]").matches(
                            current
                        )
                    ) {
                        processArray(current)
                    } else {
                        if (!code[iterator].contains(CodeBlockOperation.BEGIN.symbol) && !code[iterator].contains(
                                CodeBlockOperation.BLOCK_END.symbol
                            ) && !code[iterator].contains(
                                CodeBlockOperation.ELSE.symbol
                            )
                        ) {
                            calculateExpression(current)
                        }
                    }
                    
                    if (ifs.isNotEmpty() && (ifs[0][1] as Map<Any, Any>).containsKey(
                            iterator
                        ) && (cycles.isNotEmpty() && !(cycles[0][1] as Map<Any, Any>).containsKey(
                            iterator
                        ) || cycles.isEmpty())
                    ) {
                        iterator = (ifs[0][1] as Map<Any, Any>)[iterator] as Int
                    } else if (cycles.isNotEmpty() && (cycles[0][1] as Map<Any, Any>).containsKey(
                            iterator
                        )
                    ) {
                        iterator =
                            (cycles[0][1] as Map<Any, Any>)[iterator] as Int
                    } else {
                        iterator += 1
                    }
                }
            } catch (e: Exception) {
                return CodeResult(
                    mutableListOf(ExecuteError.RUNTIME.symbol), iterator + 1
                )
            }
        }
        
        return CodeResult(result, -1)
    }
}