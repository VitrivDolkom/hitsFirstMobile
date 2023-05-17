import java.util.*
import kotlin.math.floor
import java.lang.Integer.parseInt
import java.util.Stack
import java.util.Vector
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt
import java.util.regex.Pattern
import kotlin.math.*
import kotlin.collections.*

class Interpreter {



    var arrays = mutableMapOf<String, Pair<MutableList<String>, MutableList<String>>>()
    fun processArray(line: String) {
        var mas = ""
        var key = ""
        if ("=" in line) {
            key = line.substring(0, line.indexOf("=")).trim()
            mas = line.substring(line.indexOf("=") + 1).trim()
        }
        mas = mas.substring(1, mas.length - 1)
        val res = mutableListOf<Double>()
        mas = mas.replace(";", " ").replace(',', '.').replace("\\s+".toRegex(), " ")
        for (el in mas.split(" ")) {
            if (el != ""){
                res.add(el.toDouble())
            }

        }
        arrays[key] = Pair(res.map { it.toString() }.toMutableList(), List(res.size) { "$key[$it]" }.toMutableList())
    }

    fun polandCondition(line: String): Any {
        //print(line)
        val comparators = listOf(">", ">=", "<=", "<", "<=", "==", "!=", "and", "or", "(", ")")
        var line = line.substring(line.indexOf(" ")).replace(" ", "").replace(":","")
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
        val st = mutableListOf<String>()
        val postfix = mutableListOf<String>()
        //print(temp)
        for (el in temp) {
            if (el !in comparators) {
                postfix.add(el)
            } else {
                if (el == "(") {
                    st.add(el)
                } else if (el == ")") {
                    while (st.last() != "(") {
                        postfix.add(st.removeAt(st.lastIndex))
                    }
                    if (st.last() == "(") {
                        st.removeAt(st.lastIndex)
                    }
                }
                else{
                    while (st.isNotEmpty() && comparators.indexOf(st.last()) <= comparators.indexOf(el)) {
                        postfix.add(st.removeAt(st.lastIndex))
                    }
                    st.add(el)
                }

            }
        }
        while (st.isNotEmpty()) {
            postfix.add(st.removeAt(st.lastIndex))
        }
        val res = mutableListOf<Any>()
        for (element in postfix) {
            if (element !in comparators) {
                res.add(element)
            } else {
                val f = res.removeLast()
                val s = res.removeLast()
                when (element) {
                    ">" -> res.add(s.toString().toFloat() > f.toString().toFloat())
                    "<" -> res.add(s.toString().toFloat() < f.toString().toFloat())
                    "<=" -> res.add(s.toString().toFloat() <= f.toString().toFloat())
                    ">=" -> res.add(s.toString().toFloat() >= f.toString().toFloat())
                    "==" -> res.add(s.toString().toFloat() == f.toString().toFloat())
                    "and" -> res.add(s.toString().toBoolean() && f.toString().toBoolean())
                    "or" -> res.add(s.toString().toBoolean() || f.toString().toBoolean())
                    "!=" -> res.add(s.toString().toFloat() != f.toString().toFloat())
                }
            }
        }
        return res.last()

    }


    fun conditionFinder(code: List<String>): MutableList<List<Any>> {
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
                    while (index + k < code.size && code[index + k] == "end") {
                        k += 1
                    }
                    transitionsDictionary[index - 1] = index + k
                    if (branchChecker == -1) {
                        transitionsDictionary[laststartpoint - 1] = listOf(transitionsDictionary[laststartpoint - 1], transitionsDictionary[index - 1])
                    } else {
                        transitionsDictionary[laststartpoint - 1] = listOf(transitionsDictionary[laststartpoint - 1], branchChecker)
                        transitionsDictionary[branchChecker - 1] = transitionsDictionary[index - 1] as Any
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
                        transitionsDictionary[index - temp.size] = listOf(transitionsDictionary[index - temp.size], index + 1)
                    } else {
                        transitionsDictionary[index - temp.size] = listOf(transitionsDictionary[index - temp.size], branchChecker)
                    }
                }
                if ("if" in code[index - temp.size]) {
                    val target = if (branchChecker == -1) index + 1 else branchChecker
                    conditions.add(listOf(listOf(index - temp.size, target), transitionsDictionary.toMutableMap(), listOf(code[index - temp.size]) + temp))
                }
                transitionsDictionary.clear()
                temp.clear()
                branchChecker = -1
            }
        }
        return conditions
    }


    fun cycleFinder(code: List<String>): MutableList<List<Any>> {
        val temp = mutableListOf<String>()
        var flag = 0
        var counter = 0
        val cycl = mutableListOf<List<Any>>()
        val transitionsDictionary = mutableMapOf<Int, Any>()
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
                        transitionsDictionary[index] = laststartpoint-1
                    }
                    if (transitionsDictionary[laststartpoint - 1] is Int) {
                        transitionsDictionary[laststartpoint - 1] = listOf(transitionsDictionary[laststartpoint - 1], index + k)
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
                    transitionsDictionary[index - temp.size] = listOf(transitionsDictionary[index - temp.size], index + 1)
                }
                transitionsDictionary[index] = index - temp.size
                if ("while" in code[index - temp.size]) {
                    cycl.add(listOf(listOf(index - temp.size, index + 1), transitionsDictionary.toMutableMap(), listOf(code[index - temp.size]) + temp))
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

    var dictionary = mutableMapOf<String, Any>()



    fun CalculateExpression(expression: String): String{
        var expression = expression.replace(" ", "")
        var peremen = ""
        //print("Новая переменная $peremen\n")
        var ans = ""
        var arrkey = ""
        var arrval = ""
        if ("=" in expression) {
            peremen = expression.substring(0, expression.indexOf("="))
            ans = expression.substring(expression.indexOf("=") + 1)
            if ("[" in peremen) {
                peremen = peremen.trim()
                val spltpos = peremen.indexOf("[")
                arrkey = peremen.substring(0, spltpos)
                arrval = peremen.substring(spltpos + 1, peremen.length - 1)
            }
        } else {
            var peremen = ""
            ans = expression.substring(0)
        }



        var spltd = Vector<String>()
        var temp:List<String>

        var postfix = Vector<String>()
        val operations: String = "+-/*,%"
        var GlobalResult = ""
        if (ans != ""){

            while (operations.contains(ans[ans.length-1])){
                ans = ans.substring(0, ans.length-1)
            }

            for (elem in "+*/%()"){
                ans = ans.replace(elem.toString(), " $elem ") // разделяем операции, чтобы потом сплитнуть по ним
                // знак минус не учитываем, его обработаем отдельно,
                // чтобы грамотно отделить отрицательные числа от обычного минуса
            }

            for (el in Regex("(?<=\\[)[^\\[\\]]+(?=\\])").findAll(ans)) {
                if (el.value in dictionary) {
                    ans = ans.replace(el.value.toString(), dictionary[el.value]!!.toString())
                }
            }
            //print("Промежут $ans\n")
            for (el in Regex("[a-zA-Z]+\\[[^\\]]+\\]").findAll(ans)) {
                val key = el.value.substring(0, el.value.indexOf('['))

                if (!arrays.containsKey(key)) {
                    println("ooo $key")
                    return "Runtime Error"
                }
                var vall = el.value.substring(el.value.indexOf('[') + 1, el.value.length - 1)
                vall = CalculateExpression(vall).toFloat().toString()

                if (vall.any { it.isLetter() }) {
                    println("oooo ${el.value}")
                    return "Runtime error"
                }
                ans = ans.replace(el.value.toString(), arrays[key]!!.first[vall.toDouble().toInt()].toString())
            }


            for ((key, value) in dictionary) {
                ans = ans.replace(key.toString(), value.toString())
            }


            if (ans.any { it.isLetter() }) { // а вдруг после замены существующих переменных остались несуществующие?...
                //print("Runtime Error")
                //System.exit(0)
                return "Runtime Error" // Тогда такой код нельзя выполнить
            }


            temp = ans.split(' ')
            //print(temp) // Тут по сути нужно заменить переменные их численными значениями (если переенные есть после знака равенства)
            // замена выше ...


            //print(temp)
            for (element in temp){ // работаем с минусами
                if (element.length >= 3){
                    if (element[0] == '-'){
                        var it: Int = 1
                        while (it < element.length && !"+-×÷%".contains(element[it])){
                            it += 1
                        }
                        spltd.add(element.substring(0,it))
                        for (x in element.substring(it, element.length).replace("-", " - ").split(' ')){
                            if (x != " " && x != ""){
                                spltd.add(x)
                            }
                        }
                    }
                    else{
                        spltd.addAll(element.replace("-", " - ").split(' '))
                    }
                }
                else{
                    if (element.length == 2 && element[element.length - 1] == '-') {
                        spltd.add(element[0].toString())
                        spltd.add(element[element.length - 1].toString())
                    } else {
                        spltd.add(element)
                    }
                }
            }
            //println(spltd.toString())

//        for (value in spltd){
//            var tmp: String = value
//            var new: String = ""
//            if (tmp.contains('%')){
//                for (ind in 0..tmp.length-2){
//                    if (tmp[ind] != '%'){
//                        new += tmp[ind]
//                    }
//                    else{
//                        if (tmp[ind+1] == '%'){
//                            new += "%1"
//                        }
//                        else{
//                            new += "%"
//                        }
//                    }
//                }
//                if (tmp[tmp.length-1] == '%'){
//                    new += "%1"
//                }
//                else{
//                    new += tmp[tmp.length-1]
//                }
//                for (x in new.replace("%", " % ").split(' ')) {
//                    if (x != " " && x != "") {
//                        savedversion.add(x)
//                    }
//                }
//            }
//            else{
//                savedversion.add(tmp)
//            }
//        }
            //spltd = savedversion
            //println(spltd.toString())
            var st = Stack<String>()

            val spltd = spltd.filter { it.isNotEmpty() }
            // теперь создаем польскую нотацию
            for (expr in spltd){
                if (!"+-*/%()".contains(expr)){

                    var t: String = expr
                    if (expr[expr.length-1] == ','){
                        t = expr.substring(0, expr.length-1)
                    }
                    if (expr.contains(',')){
                        t = expr.replace(',','.')
                    }
                    //println(t.contains(','))
                    postfix.add(t)
                }
                else{
                    if (expr == "(") {
                        st.add(expr)
                    } else if (expr == ")") {
                        while (st.last() != "(") {
                            postfix.add(st.removeAt(st.lastIndex))
                        }
                        if (st.last() == "(") {
                            st.removeAt(st.lastIndex)
                        }
                    }
                    else{
                        while (!st.isEmpty() && ("+-".contains(expr) && "*/%".contains(st.peek())||
                                    "+-".contains(expr) && "+-".contains(st.peek()) ||
                                    "*/%".contains(expr) && "*/%".contains(st.peek()))){
                            postfix.add(st.pop())
                        }

                        st.add(expr)
                    }

                }
            }
            while (!st.isEmpty()){
                postfix.add(st.pop())
            }
            var flag: Boolean = true
            var res = Stack<String>()
            for (elem in postfix){ // а теперь по польской нотации считаем ответ
                if (!"+-*/%".contains(elem)){
                    if (elem == "-0" || elem.toDouble()-0.0 == 0.0){
                        res.add("0")
                    }
                    else{
                        res.add(elem)
                    }

                }
                else{
                    var f: String = res.pop()
                    var s: String = res.pop()

                    if (elem == "+"){
                        res.push((f.toDouble() + s.toDouble()).toString())
                    }
                    if (elem == "-"){
                        res.push((s.toDouble() - f.toDouble()).toString())
                    }
                    if (elem == "*"){
                        res.push((f.toDouble() * s.toDouble()).toString())
                    }
                    if (elem == "%"){
                        //res.push((s.toDouble()*f.toDouble()/100.0).toString())
                        if (f == "0" || f == "-0"){
                            GlobalResult = "Error"

                            flag = false
                            break
                        }
                        else{
                            res.push((s.toDouble()%f.toDouble()).toString())
                        }

                    }
                    if (elem == "/"){
                        if (f == "0" || f == "-0"){
                            GlobalResult = "Error"

                            flag = false
                            break
                        }
                        //res.push((s.toDouble() / f.toDouble()).toString())
                        res.push(floor(s.toDouble().div(f.toDouble())).toString())
                    }
                }
            }
            //println(res)
            if (flag){
                GlobalResult = res.pop().replace(',','.')
            }
            if (peremen != ""){
                if (arrval != ""){
                    val arrval = CalculateExpression(arrval).toFloat().toInt()
                    if (!arrval.toString().any{it.isLetter()} && "${arrkey}[${arrval}]" in arrays[arrkey]!!.second) {
                        arrays[arrkey]!!.first[arrval] = GlobalResult
                    }
                }
                else{
                    dictionary[peremen] = GlobalResult
                }
            }


        }
        return GlobalResult
    }




    fun executor(code: MutableList<String>): MutableList<String>{

//    print("Введите выражение: ")
//    var expr: String = readLine()!!
//    //CheckExpression(expr)
//    print(CalculateExpression(expr))c
        //val code = mutableListOf<String>()
        //var s = readLine()!!
        //code.add(s)
        //while (s != "") {
        //s = readLine()!!
        //code.add(s)
        //}
        if (code.isNotEmpty() && code[code.size-1] == ""){
            code.removeLast()
        }

        var ifs = conditionFinder(code)
        var cycles = cycleFinder(code)

        var iterator = 0
        var nextline = 1
        //code.removeLast()
        //println(ifs)
        //println(cycles)
        val result = mutableListOf<String>()
        while (iterator < code.size) {
            val current = code[iterator].trim()
            //print(current)
            if (ifs.isNotEmpty() && (((iterator >= (ifs[0][0] as List<Int>)[1]) && (cycles.isEmpty() || cycles.isNotEmpty() && (iterator >= (cycles[0][0] as List<Int>)[1]))) || cycles.isNotEmpty() && ((cycles[0][0] as List<Int>)[1] >= (ifs[0][0] as List<Int>)[1]) && iterator==(cycles[0][0] as List<Int>)[1])) {
                //ifs.removeLast()
                ifs.removeAt(0)
            }
            if (cycles.isNotEmpty() && ((iterator >= (cycles[0][0] as List<Int>)[1]) || cycles.size >= 2 && (cycles[1][0] as List<Int>)[0] == iterator)) {
                //cycles.removeLast()
                cycles.removeAt(0)
            }
            if (ifs.isNotEmpty() && "if" in current) {
                val getres = polandCondition(current)
                if (getres == true) {
                    iterator = ((ifs[0][1] as Map<Any, Any>)[iterator] as List<Int>)[0] as Int
                } else {
                    iterator = ((ifs[0][1] as Map<Any, Any>)[iterator] as List<Int>)[1] as Int
                }
            } else if (cycles.isNotEmpty() && "while" in current) {
                val getres = polandCondition(current)
                if (getres == true) {
                    iterator = ((cycles[0][1] as Map<Any, Any>)[iterator] as List<Int>)[0] as Int
                } else {
                    iterator = ((cycles[0][1] as Map<Any, Any>)[iterator] as List<Int>)[1] as Int
                }
            } else {

                if (Regex("print\\s\\(.+\\)").matches(current)) {
                    //show(current)
                    result.add(show(current))
                } else if (Regex("[a-zA-Z]+(\\s|)\\=(\\s|)\\[(\\s|)([a-zA-Z0-9\\.,]+(\\s|)\\;(\\s|))*[0-9]+(\\s|)\\]").matches(current)) {
                    processArray(current)
                } else {

                    CalculateExpression(current)
                }
                if (ifs.isNotEmpty() && (ifs[0][1] as Map<Any, Any>).containsKey(iterator) && (cycles.isNotEmpty() && !(cycles[0][1] as Map<Any, Any>).containsKey(iterator) || cycles.isEmpty())) {
                    iterator = (ifs[0][1] as Map<Any, Any>)[iterator] as Int
                } else if (cycles.isNotEmpty() && (cycles[0][1] as Map<Any, Any>).containsKey(iterator)) {
                    iterator = (cycles[0][1] as Map<Any, Any>)[iterator] as Int
                } else {
                    iterator += 1
                }
            }
        }
        //println(dictionary)
        //println(arrays)

        return result

    }

}
