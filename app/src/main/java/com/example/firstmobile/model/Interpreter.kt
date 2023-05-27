package com.example.firstmobile.model

import kotlin.math.floor
import java.util.Stack
import java.util.Vector
import java.util.regex.Pattern
import kotlin.collections.*

data class CodeResult(var result: MutableList<String>, var errorline: Int)

class Interpreter {
    var arrays = mutableMapOf<String, Pair<MutableList<String>, MutableList<String>>>()

    fun processArray(line: String) {
        var array = ""
        var name = ""
        if ("=" in line) {
            name = line.substring(0, line.indexOf("=")).trim()
            array = line.substring(line.indexOf("=") + 1).trim()
        }
        array = array.substring(1, array.length - 1)
        val res = mutableListOf<Double>()
        array = array.replace(";", " ").replace(',', '.')
            .replace("\\s+".toRegex(), " ")
        for (element in array.split(" ")) {
            if (element != ""){
                res.add(element.toDouble())
            }

        }
        arrays[name] = Pair(res.map { it.toString() }.toMutableList(),
            List(res.size) { "$name[$it]" }.toMutableList()
        )
    }
    
    fun polandCondition(line: String): Any { // Исходя из задуманной логики, может возвращаться разный тип данных
        //print(line)
        val comparators = listOf(
            ">", ">=", "<=", "<", "<=", "==", "!=", "and", "or", "(", ")"
        )
        var line =
            line.substring(line.indexOf(" ")).replace(" ", "").replace(":", "")
        //print("Это условие $line")
        for (element in comparators.reversed()) {
            line = line.replace(element, " $element ")
        }
        val temp = mutableListOf<String>()
        val lineList = line.split(" ")
        val vremen = mutableListOf<String>()
        var i = 0
        while (i < lineList.size - 1) {
            if (lineList[i] + lineList[i + 1] in comparators) {
                vremen.add(lineList[i] + lineList[i + 1])
                i += 2
            } else {
                vremen.add(lineList[i])
                i += 1
            }
        }
        while (i < lineList.size) {
            vremen.add(lineList[i])
            i += 1
        }
        val newLine = vremen.toList()
        //print("New line $newLine")
        for (el in newLine) {
            if (el !in comparators && !"()".contains(el)) {
                temp.add(CalculateExpression(el).replace(",", "."))
            } else {
                temp.add(el)
            }
        }
        val ComparatorsStack = mutableListOf<String>()
        val postfix = mutableListOf<String>()
        for (element in temp) {
            if (element !in comparators) {
                postfix.add(element)
            } else {
                if (element == "(") {
                    ComparatorsStack.add(element)
                } else if (element == ")") {
                    while (ComparatorsStack.last() != "(") {
                        postfix.add(ComparatorsStack.removeAt(ComparatorsStack.lastIndex))
                    }
                    if (ComparatorsStack.last() == "(") {
                        ComparatorsStack.removeAt(ComparatorsStack.lastIndex)
                    }
                } else {
                    while (ComparatorsStack.isNotEmpty() && comparators.indexOf(ComparatorsStack.last()) <= comparators.indexOf(
                            element
                        )
                    ) {
                        postfix.add(ComparatorsStack.removeAt(ComparatorsStack.lastIndex))
                    }
                    ComparatorsStack.add(element)
                }
            }
        }
        while (ComparatorsStack.isNotEmpty()) {
            postfix.add(ComparatorsStack.removeAt(ComparatorsStack.lastIndex))
        }
        val ResultStack = mutableListOf<Any>()
        for (element in postfix) {
            if (element !in comparators) {
                ResultStack.add(element)
            } else {
                val f = ResultStack.removeLast().toString().replace("false","0").replace("true","1")
                val s = ResultStack.removeLast().toString().replace("false", "0").replace("true", "1")
                when (element) {
                    ">" -> ResultStack.add(
                        s.toString().toFloat() > f.toString().toFloat()
                    )
                    "<" -> ResultStack.add(
                        s.toString().toFloat() < f.toString().toFloat()
                    )
                    "<=" -> ResultStack.add(
                        s.toString().toFloat() <= f.toString().toFloat()
                    )
                    ">=" -> ResultStack.add(
                        s.toString().toFloat() >= f.toString().toFloat()
                    )
                    "==" -> ResultStack.add(
                        s.toString().toFloat() == f.toString().toFloat()
                    )
                    "and" -> ResultStack.add(
                        (s.toString().toFloat() != 0.toFloat()) && (f.toString().toFloat() != 0.toFloat())
                    )
                    "or" -> ResultStack.add(
                        (s.toString().toFloat() != 0.toFloat()) || (f.toString().toFloat() != 0.toFloat())
                    )
                    "!=" -> ResultStack.add(
                        s.toString().toFloat() != f.toString().toFloat()
                    )
                }
            }
        }
        return ResultStack.last()
    }

    fun conditionFinder(code: List<String>): MutableList<List<Any>> { // Используем Any, из-за сложной структуризации данных о циклах и условиях
        val temp = mutableListOf<String>()
        var flag = 0
        var counter = 0
        val conditions = mutableListOf<List<Any>>()
        val transitionsDictionary = mutableMapOf<Int, Any>()
        var laststartpoint = -1
        var branchChecker = -1
        for ((index, line) in code.withIndex()) {
            if ("else" in line) {
                branchChecker = index + 1
            }
            
            if ("begin" in line && "if" in code[index - 1]) {
                flag = 1
                counter += 1
                laststartpoint = index
                transitionsDictionary[index - 1] = index + 1
            }
            if ("end" in line && flag == 1) {
                flag = 1
                counter -= 1
                var k = 0
                if (code[index - 1] != "end") {
                    //while (index + k < code.size && code[index + k] == "end") {
                    //k += 1
                    //}
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
                if ("if" in code[index - temp.size]) {
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

    fun cycleFinder(code: List<String>): MutableList<List<Any>> { // Используем Any, аналогично с поиском условий
        val temp = mutableListOf<String>()
        var flag = 0
        var counter = 0
        val cycl = mutableListOf<List<Any>>()
        val transitionsDictionary = mutableMapOf<Int, Any>() // Тип значения может меняться (если обычная строка - Int, если while - то list
        var laststartpoint = -1
        val st = mutableListOf<String>()
        for ((index, line) in code.withIndex()) {
            if ("begin" in line && "if" in code[index - 1]) {
                st.add("if")
            }
            if ("begin" in line && "while" in code[index - 1]) {
                st.add("while")
                flag = 1
                counter += 1
                laststartpoint = index
                transitionsDictionary[index - 1] = index + 1
            }
            if ("end" in line && flag == 1 && st.removeAt(st.lastIndex) == "while") {
                flag = 1
                counter -= 1
                var k = 0
                if (code[index - 1] != "end") {
                    k = 1
                    transitionsDictionary[index - 1] = index
                    if ("if" !in code[laststartpoint - 1]) {
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
                if ("while" in code[index - temp.size]) {
                    cycl.add(
                        listOf(
                            listOf(index - temp.size, index + 1),
                            transitionsDictionary.toMutableMap(),
                            listOf(code[index - temp.size]) + temp
                        )
                    )
                }
                transitionsDictionary.clear()
                temp.clear()
                var secflag = 0
            }
        }
        return cycl
    }

    fun show(obj: String): String {
        val pattern = Pattern.compile("(?<=\\(\\s).+(?=\\s\\))")
        val matcher = pattern.matcher(obj)
        if (matcher.find()) {
            var a = matcher.group()
            if (a in arrays) {
                return (arrays[a]?.first.toString())
            } else if (a in dictionary) {
                return (dictionary[a].toString())
            } else {
                return (CalculateExpression(a))
            }
        }
        return "Runtime Error"
    }

    var dictionary = mutableMapOf<String, Any>() // Используем Any, т.к. по задуманной релизации в паре ключ-значение значение может быть либо Int, либо List

    fun CalculateExpression(expression: String): String {
        var expression = expression.replace(" ", "").replace(",",".")
        var peremen = ""
        //print("Новая переменная $peremen\n")
        var answer = ""
        var arrkey = ""
        var arrval = ""
        if ("=" in expression) {
            peremen = expression.substring(0, expression.indexOf("="))
            answer = expression.substring(expression.indexOf("=") + 1)
            if ("[" in peremen) {
                peremen = peremen.trim()
                val spltpos = peremen.indexOf("[")
                arrkey = peremen.substring(0, spltpos)
                arrval = peremen.substring(spltpos + 1, peremen.length - 1)
            }
        } else {
            var peremen = ""
            answer = expression.substring(0)
        }

        var SplittedVector = Vector<String>()
        var temp: List<String>
        
        var postfix = Vector<String>()
        val operations: String = "+-/*,%"
        var GlobalResult = ""
        if (answer != "") {
            
            while (operations.contains(answer[answer.length - 1])) {
                answer = answer.substring(0, answer.length - 1)
            }


            for (elem in "+*/%()") {
                answer = answer.replace(
                    elem.toString(),
                    " $elem "
                ) // разделяем операции, чтобы потом сплитнуть по ним
                // знак минус не учитываем, его обработаем отдельно,
                // чтобы грамотно отделить отрицательные числа от обычного минуса
            }

            for (element in Regex("(?<=\\[)[^\\[\\]]+(?=\\])").findAll(answer)) {
                if (element.value in dictionary) {
                    answer = answer.replace(
                        element.value.toString(),
                        dictionary[element.value]!!.toString()
                    )
                }
            }
            //print("Промежут $ans\n")
            for (element in Regex("[a-zA-Z]+\\[[^\\]]+\\]").findAll(answer)) {
                val key = element.value.substring(0, element.value.indexOf('['))


                var vallue = element.value.substring(
                    element.value.indexOf('[') + 1,
                    element.value.length - 1
                )
                vallue = CalculateExpression(vallue).toFloat().toString()


                answer = answer.replace(
                    element.value.toString(),
                    arrays[key]!!.first[vallue.toDouble().toInt()].toString()
                )
            }
            
            
            for ((key, value) in dictionary) {
                answer = answer.replace(key.toString(), value.toString())
            }

            temp = answer.split(' ')
            
            for (element in temp) { // работаем с минусами
                if (element.length >= 3) {
                    if (element[0] == '-') {
                        var it: Int = 1
                        while (it < element.length && !"+-×÷%".contains(element[it])) {
                            it += 1
                        }
                        SplittedVector.add(element.substring(0, it))
                        for (x in element.substring(it, element.length)
                            .replace("-", " - ").split(' ')) {
                            if (x != " " && x != "") {
                                SplittedVector.add(x)
                            }
                        }
                    } else {
                        SplittedVector.addAll(element.replace("-", " - ").split(' '))
                    }
                } else {
                    if (element.length == 2 && element[element.length - 1] == '-') {
                        SplittedVector.add(element[0].toString())
                        SplittedVector.add(element[element.length - 1].toString())
                    } else {
                        SplittedVector.add(element)
                    }
                }
            }
            
            var ComparatorsStack = Stack<String>()

            val spltd = SplittedVector.filter { it.isNotEmpty() }
            // теперь создаем польскую нотацию
            for (expr in spltd) {
                if (!"+-*/%()".contains(expr)) {
                    var t: String = expr
                    if (expr[expr.length - 1] == ',') {
                        t = expr.substring(0, expr.length - 1)
                    }
                    if (expr.contains(',')) {
                        t = expr.replace(',', '.')
                    }
                    //println(t.contains(','))
                    postfix.add(t)
                } else {
                    if (expr == "(") {
                        ComparatorsStack.add(expr)
                    } else if (expr == ")") {
                        while (ComparatorsStack.last() != "(") {
                            postfix.add(ComparatorsStack.removeAt(ComparatorsStack.lastIndex))
                        }
                        if (ComparatorsStack.last() == "(") {
                            ComparatorsStack.removeAt(ComparatorsStack.lastIndex)
                        }
                    } else {
                        while (!ComparatorsStack.isEmpty() && ("+-".contains(expr) && "*/%".contains(
                                ComparatorsStack.peek()
                            ) || "+-".contains(expr) && "+-".contains(ComparatorsStack.peek()) || "*/%".contains(
                                expr
                            ) && "*/%".contains(ComparatorsStack.peek()))
                        ) {
                            postfix.add(ComparatorsStack.pop())
                        }

                        ComparatorsStack.add(expr)
                    }
                }
            }
            while (!ComparatorsStack.isEmpty()) {
                postfix.add(ComparatorsStack.pop())
            }
            var flag: Boolean = true
            var ResultStack = Stack<String>()
            for (elem in postfix) { // а теперь по польской нотации считаем ответ
                if (!"+-*/%".contains(elem)) {
                    if (elem == "-0" || elem.toDouble() - 0.0 == 0.0) {
                        ResultStack.add("0")
                    } else {
                        ResultStack.add(elem)
                    }
                } else {
                    var FirstOperand: String = ResultStack.pop()
                    var SecondOperand: String = ResultStack.pop()

                    if (elem == "+") {
                        ResultStack.push((FirstOperand.toDouble() + SecondOperand.toDouble()).toString())
                    }
                    if (elem == "-") {
                        ResultStack.push((SecondOperand.toDouble() - FirstOperand.toDouble()).toString())
                    }
                    if (elem == "*") {
                        ResultStack.push((FirstOperand.toDouble() * SecondOperand.toDouble()).toString())
                    }
                    if (elem == "%") {

                        ResultStack.push((SecondOperand.toDouble() % FirstOperand.toDouble()).toString())
                    }
                    if (elem == "/") {
//
                        ResultStack.push(
                            floor(
                                SecondOperand.toDouble().div(FirstOperand.toDouble())
                            ).toString()
                        )
                    }
                }
            }
            //println(res)
            if (flag) {
                GlobalResult = ResultStack.pop().replace(',', '.')
            }
            if (peremen != "") {
                if (arrval != "") {
                    val arrval = CalculateExpression(arrval).toFloat().toInt()
                    if (!arrval.toString()
                            .any { it.isLetter() } && "${arrkey}[${arrval}]" in arrays[arrkey]!!.second
                    ) {
                        arrays[arrkey]!!.first[arrval] = GlobalResult
                    }
                } else {
                    dictionary[peremen] = GlobalResult
                }
            }
        }
        return GlobalResult
    }

    fun executor(code: MutableList<String>): CodeResult {


        if (code.isNotEmpty() && code[code.size - 1] == "") {
            code.removeLast()
        }
        var ifs = conditionFinder(code)
            var cycles = cycleFinder(code)
        
        var iterator = 0
        var nextline = 1

        val result = mutableListOf<String>()

        while (iterator < code.size) {
            try{
                val current = code[iterator].trim()
                //print(current)
                if (ifs.isNotEmpty() && (((iterator >= (ifs[0][0] as List<Int>)[1]) && (cycles.isEmpty() || cycles.isNotEmpty() && (iterator >= (cycles[0][0] as List<Int>)[1]))) || cycles.isNotEmpty() && ((cycles[0][0] as List<Int>)[1] >= (ifs[0][0] as List<Int>)[1]) && iterator == (cycles[0][0] as List<Int>)[1])) {

                    ifs.removeAt(0)
                }
                if (cycles.isNotEmpty() && ((iterator >= (cycles[0][0] as List<Int>)[1]) || cycles.size >= 2 && (cycles[1][0] as List<Int>)[0] == iterator)) {

                    cycles.removeAt(0)
                }
                if (ifs.isNotEmpty() && "if" in current) {
                    val getres = polandCondition(current)
                    if (getres == true) {
                        iterator =
                            ((ifs[0][1] as Map<Any, Any>)[iterator] as List<Int>)[0] as Int
                    } else {
                        iterator =
                            ((ifs[0][1] as Map<Any, Any>)[iterator] as List<Int>)[1] as Int
                    }
                } else if (cycles.isNotEmpty() && "while" in current) {
                    val getres = polandCondition(current)
                    if (getres == true) {
                        iterator =
                            ((cycles[0][1] as Map<Any, Any>)[iterator] as List<Int>)[0] as Int
                    } else {
                        iterator =
                            ((cycles[0][1] as Map<Any, Any>)[iterator] as List<Int>)[1] as Int
                    }
                } else {
                    if (Regex("print\\s\\(.+\\)").matches(current)) {

                        result.add(show(current))
                    } else if (Regex("[a-zA-Z]+(\\s|)\\=(\\s|)\\[(\\s|)([a-zA-Z0-9\\.,]+(\\s|)\\;(\\s|))*[0-9]+(\\s|)\\]").matches(
                            current
                        )
                    ) {
                        processArray(current)
                    } else {
                        if (!code[iterator].contains("begin") && !code[iterator].contains("end") && !code[iterator].contains(
                                "else"
                            )
                        ) {
                            CalculateExpression(current)
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
                        iterator = (cycles[0][1] as Map<Any, Any>)[iterator] as Int
                    } else {
                        iterator += 1
                    }
                }
            } catch (e: Exception) {

                return CodeResult(mutableListOf("Runtime Error"), iterator+1)
            }
        }



        return CodeResult(result, -1)
    }
}