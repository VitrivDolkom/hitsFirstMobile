package com.example.firstmobile
import java.util.Stack
import java.util.Vector
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt
class Interpreter {


    fun CheckExpression(expression: String): String {
        val perem = "(?:[a-zA-Z]+[a-zA-Z0-9]*)"
        val perem_without_brackets = "[a-zA-Z]+[0-9A-Za-z]*"
        val nums_with_one = "[0-9]*"
        val nums = "[0-9]*"
        var operations = "[\\+\\-*\\/]"
        operations = "\\s*(?:$perem|$nums_with_one|$perem_without_brackets\\[(?:$perem_without_brackets|$nums)\\])\\s*(?:$operations\\s*(?:$perem|$nums_with_one|$perem_without_brackets\\[(?:$perem_without_brackets|$nums)\\])\\s*)*"
        val formas = "(?:$perem_without_brackets\\[$operations\\]\\s*=\\s*(?:$perem|$nums_with_one|$perem_without_brackets\\[$operations\\])\\s*(?:$operations\\s*(?:$perem|$nums_with_one|$perem_without_brackets\\[$operations\\])\\s*)*;)"
        val forperem = "(?:$perem\\s*=\\s*(?:$perem|$nums_with_one|$perem_without_brackets\\[$operations\\])\\s*(?:$operations\\s*(?:$perem|$nums_with_one|$perem_without_brackets\\[$operations\\])\\s*)*;)"

        val expressions = Regex("$forperem|$formas").findAll(expression!!)
        for (el in expressions) {
            println(el.value)
        }
        return ""
    }

    fun CalculateExpression(expression: String): String{
        var expression = expression.replace(" ", "")
        var peremen = expression.substring(0,expression.indexOf("="))
        //print("Новая переменная $peremen\n")
        var ans = expression.substring(expression.indexOf("=")+1, expression.length)
        var spltd = Vector<String>()
        var savedversion = Vector<String>()
        var temp:List<String>

        var postfix = Vector<String>()
        val operations: String = "+-÷×,%"
        var GlobalResult = ""
        if (ans != ""){

            while (operations.contains(ans[ans.length-1])){
                ans = ans.substring(0, ans.length-1)
            }

            for (elem in "+×÷%"){
                ans = ans.replace(elem.toString(), " $elem ") // разделяем операции, чтобы потом сплитнуть по ним

            }
            for (key in map.keys){
                ans = ans.replace(key, map[key]!!)
            }
            temp = ans.split(' ')
            //print(temp) // Тут по сути нужно заменить переменные их численными значениями (если переенные есть после знака равенства)




            for (element in temp){
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
                    spltd.add(element)
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



            for (expr in spltd){
                if (!"+-×÷%".contains(expr)){

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
                    while (!st.isEmpty() && ("+-".contains(expr) && "×÷%".contains(st.peek())||
                                "+-".contains(expr) && "+-".contains(st.peek()) ||
                                "×÷%".contains(expr) && "×÷%".contains(st.peek()))){
                        postfix.add(st.pop())
                    }

                    st.add(expr)
                }
            }
            while (!st.isEmpty()){
                postfix.add(st.pop())
            }
            var flag: Boolean = true
            var res = Stack<String>()
            for (elem in postfix){
                if (!"+-×÷%".contains(elem)){
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
                    if (elem == "×"){
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
                    if (elem == "÷"){
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
            if (flag){
                GlobalResult = res.pop().replace('.',',')
            }

        }
        map[peremen] = GlobalResult
        return GlobalResult
    }

    var map = mutableMapOf<String, String>()
    fun main(){

//    print("Введите выражение: ")
//    var expr: String = readLine()!!
//    //CheckExpression(expr)
//    print(CalculateExpression(expr))c
        var code = Vector<String>()

        print("Введите код: \n")
        var input = readLine()!!
        print(CalculateExpression(input))
        print('\n')
        while (input != null && input.isNotEmpty()) {
            code.add(input)
            input = readLine()!!
            //print("Ввод $input\n")
            print(CalculateExpression(input))
            print('\n')
        }
        print(code)
    }

}