from math import *
from re import *
# Идеи по коду (до написания):
# мы идём по строкам...
# если это обычное присвавание, то мы присваиваем и записываем в словарик

# если встретилось условие...
# то можно переключиться на другую сканнирующую функцию,
# а дальше как-то сложно пока что..
# как будто бы для каждого встречного if нужно
# понимать, что на следующей строчке находится begin

### UPDATE! Объяснение логики после написания кода и реализации
# Идея в том, что когда мы принимаем код (в виде массива строк), то
# мы заводим массив для блоков условий - Ifs, и массив для блоков циклов - Cycles
# После чего запускаем две соответствующие функции-сканнера, в которых мы выделяем
# эти блоки засчёт ключевых слов begin и end (в условиях в т.ч. и else)
# В этих функциях помимо базовой реализации сканнера, мы контролируем к какому
# блоку относятся ключевые слова. Такой контроль происходит за счет стека.

# Переменные и массивы хранятся в словарях (map).

# Сам код обрабатывается специальным индексом-итератором iterator, который перемещается
# по определенным правилам. Правила перемещения индекса хранятся в графах переходов соответствующих блоков.
# Графы переходов хранятся в блоках таким образом: [(глобальное начало блока, глобальный конец блока), {map: ключ - стартовая строка, value - конечная}
#, сам текст блока]. То есть, имея такие графы, мы можем управлять индексом iterator и засчёт этого перемещаться по коду.
# Причем понятно, что граф в условиях вынужден в дерево, а граф в циклах имеет цикл.

# Важно понимать, что ключевая функция для подсчёта АРИФМЕТИЧЕСКИХ выражений является рекурсивной (напр. чтобы обработать a[t+1] и тд.)

# Пока что в коде нет обработки критических случаев ошибок. 

# Также важно, что в такой реализации в польской нотации участвуют заранее заготовленные числовые значения и операнды.
# То есть, элементы массивов и переменные обрабатываются сразу после поступления на вход строки интерпретатора.







arrays = {}

def processArray(line):
 
    line = line.replace(" ", "")

    if "=" in line:
        key = line[:line.find("=")]
        mas = line[line.find("=")+1:]
    else:
        key = None
        
    mas = mas[1:-1]
    res = []
    #print(mas)
    mas = mas.replace(";", " ").replace(',','.').split()
    
    for el in mas:
        res.append(float(el))
    
    arrays[key] = [mas, [f"{key}[{i}]" for i in range(len(mas))]]
    #print(arrays)




def polandCondition(line):
    #print(line)
    comparators = ['>', '>=', '<=', '<', '<=', '==', '!=', 'and', 'or']
    #line = line[2:]
    line = line[line.find(" "):]
    line = line.strip().replace(" ", "").replace(":","")
    
    for element in comparators[::-1]:
        line = line.replace(element, f' {element} ')
    temp = []
    line = line.split()
    #print(line)
    #####
    vremen = []
    i = 0
    while i < len(line)-1:
        if line[i]+line[i+1] in comparators:
            vremen.append(line[i]+line[i+1])
            i += 2
        else:
            vremen.append(line[i])
            i += 1
    while i < len(line):
        vremen.append(line[i])
        i += 1
    line = vremen[:]
    #print(line)
    ####
    #print(line)
    for el in line:
        if el not in comparators:
            #print(el)
            
            temp.append(CalculateExpression(el).replace(",","."))
            #temp.append(el)
        else:
            temp.append(el)

    st = []
    postfix = []
    #print(temp)
    for el in temp:
        if el not in comparators:
            postfix.append(el)
        else:
            while st and comparators.index(st[-1]) <= comparators.index(el):
                postfix.append(st.pop())
            st.append(el)
    while st:
        postfix.append(st.pop())

    #print(postfix)
    res = []

    for element in postfix:
        if element not in comparators:
            res.append(element)
        else:
            f = res.pop()
            s = res.pop()
            if element == ">":
                res.append(float(s) > float(f))
            elif element == "<":
                res.append(float(s) < float(f))
            elif element == "<=":
                res.append(float(s) <= float(f))
            elif element == ">=":
                res.append(float(s) >= float(f))
            elif element == "==":
                res.append(float(s) == float(f))
                #print(res[-1])
            elif element == "and":
                res.append(bool(float(s) and float(f)))
                
            elif element == "or":
                res.append(float(s) or float(f))
            elif element == "!=":
                res.append(float(s) != float(f))
            
    return res[-1]


def conditionChecker(code):
    counter = 0
    for line in code:
        if "begin" in line:
            counter += 1

        elif "end" in line:
            counter -= 1

        if counter < 0:
            return False
    return True


def conditionFinder(code):
    temp = []
    flag = 0
    counter = 0
    conditions = []
    transitionsDictionary = {}
    laststartpoint = -1

    branchChecker = -1
    for index, line in enumerate(code):
        ### ОБНОВЛЕНИЕ! "Else" condition
        if 'else' in line:
            branchChecker = index+1


        ###
        '''if 'if' in line:
            flag = 0
            counter = 0
            temp = []
            if any("while" in code[el] for el in transitionsDictionary):
                transitionsDictionary = {}
                #temp = []'''
        if 'begin' in line and 'if' in code[index-1]: # Если встретили начало условия, то в граф переходов записваем переход на следующую после begin строку, сохранив начало подблока
            flag = 1
            counter += 1
            laststartpoint = index
            
            transitionsDictionary[index-1] = index+1
        if 'end' in line and flag: # встретили конец подблока, начинаем счетчиком k искать место для следующей позиции интерпретатора, и добавляем переход в словарь
            flag = 1
            counter -= 1
            k = 0
            #print(index, line)
            if code[index-1] != "end":
                while index + k < len(code) and code[index+k] == 'end':
                    k += 1
                transitionsDictionary[index-1] = index+k
                #if isinstance(transitionsDictionary[laststartpoint-1], int):
                if branchChecker == -1:
                    transitionsDictionary[laststartpoint-1] = [transitionsDictionary[laststartpoint-1], transitionsDictionary[index-1]]
                else:
                    transitionsDictionary[laststartpoint-1] = [transitionsDictionary[laststartpoint-1], branchChecker]
                    transitionsDictionary[branchChecker-1] = transitionsDictionary[index-1]
                #print(transitionsDictionary[laststartpoint-1])
        if flag: # если строчка входит в подблок, добавляем её и проставляем переход
            temp.append(line)
            if index-1 in transitionsDictionary.values() and (index-1 not in transitionsDictionary):
                transitionsDictionary[index-1] = index

        if counter == 0 and temp: # Глобальный блок закончился, пора корректировать связи и добавлять этот блок в список условий кода. Не забыть очистить словари и временную память для блоков 
            flag = 0
            #print(temp, counter)
            if transitionsDictionary[index-len(temp)] == index+1 or isinstance(transitionsDictionary[index-len(temp)], int):
                #print(transitionsDictionary[index-len(temp)])
                if branchChecker == -1:
                    transitionsDictionary[index-len(temp)] = [transitionsDictionary[index-len(temp)],index+1]
                else:
                    transitionsDictionary[index-len(temp)] = [transitionsDictionary[index-len(temp)], branchChecker]
            if 'if' in code[index-len(temp)]:
                #print(transitionsDictionary)
                if branchChecker == -1:
                    target = index+1
                else:
                    target = branchChecker
                    
                #conditions.append([(index-len(temp), index + 1),transitionsDictionary,[code[index-len(temp)]]+temp])
                conditions.append([(index-len(temp), target),transitionsDictionary,[code[index-len(temp)]]+temp])    
            transitionsDictionary = {}
            temp = []
            branchChecker = -1
            
    return conditions



def cycleFinder(code):
    temp = []
    flag = 0
    counter = 0
    cycl = []
    transitionsDictionary = {}
    laststartpoint = -1
    st = []
    for index, line in enumerate(code):
        '''if 'while' in line:
            flag = 0
            counter = 0
            temp = []
            if any("if" in code[el] for el in transitionsDictionary):
                transitionsDictionary = {}'''
        #if 'if' in line:
            #flag = 0
        #if 'begin' in line and 'if' in code[index-1]:
            #counter -= 1
        if 'begin' in line and 'if' in code[index-1]:
            st.append('if')
            
        if 'begin' in line and 'while' in code[index-1]:
            st.append('while')
            flag = 1
            counter += 1
            laststartpoint = index
            transitionsDictionary[index-1] = index+1
        
        if 'end' in line and flag and st.pop() == 'while':
            flag = 1
            counter -= 1
            k = 0
            if code[index-1] != "end": # Это конец подблока цикла. Но здесь отличие. Нам конечно все еще интересен индекс выхода из блока, но было бы неплохо вернуться из строчки end в строчку цикла и уже там проверить условие.
                #while index + k < len(code) and code[index+k] == 'end':
                    #k += 1
                k = 1
                #transitionsDictionary[index-1] = index+k
                transitionsDictionary[index-1] = index
                if "if" not in code[laststartpoint-1]:
                    transitionsDictionary[index] = laststartpoint-1 # вернёмся к ближайшему сверху циклу и уже там потом проверим условие
                if isinstance(transitionsDictionary[laststartpoint-1], int):
                    transitionsDictionary[laststartpoint-1] = [transitionsDictionary[laststartpoint-1], index+k]
        #if 'end' in line and not flag:
            #flag = 1
            #secflag = 1
            
        
        if flag:
            temp.append(line)
            if index-1 in transitionsDictionary.values() and (index-1 not in transitionsDictionary):
                transitionsDictionary[index-1] = index

        if counter == 0 and temp:
            flag = 0
            print(temp, index, counter)
            if transitionsDictionary[index-len(temp)] == index+1 or isinstance(transitionsDictionary[index-len(temp)], int):
                
                transitionsDictionary[index-len(temp)] = [transitionsDictionary[index-len(temp)],index+1]
                
            transitionsDictionary[index] = index-len(temp) # Не забываем для глобального подблока замкнуть end на свой цикл. Туда вернёмся, все проверим и уже там примем решение.
            
            if 'while' in code[index-len(temp)]:
                #print(transitionsDictionary)
                cycl.append([(index-len(temp), index + 1),transitionsDictionary,[code[index-len(temp)]]+temp])
            transitionsDictionary = {}
            temp = []
            secflag = 0

    return cycl 

def show(obj):
    a = search(r'(?<=\().+(?=\))', obj)
    
    print(dictionary[a[0]])


dictionary = {}
def CalculateExpression(expression: str) -> str:
    #print("Expr:", expression, dictionary)
    expression = expression.replace(" ", "")
    arrkey = ''
    arrval = ''
    if "=" in expression:
        peremen = expression[:expression.find("=")]
        ans = expression[expression.find("=")+1:]
        if '[' in peremen:
            peremen = peremen.strip()
            spltpos = peremen.find('[')
            arrkey = peremen[:spltpos]
            arrval = peremen[spltpos+1:len(peremen)-1]
            #print(arrkey, arrval)
            
    else:
        peremen = None
        ans = expression
    #print(f'ans: {ans}')
    spltd = []
    temp = []

    postfix = []
    operation = "+-/*"

    GlobalResult = ""

    if ans != "":
        # Сейчас обрежем операции справа
        # реализовано под калькулятор

        while ans[-1] in operation:
            ans = ans[:-1]
           
        # А теперь разграничим те операции, которые удобно разграничить сразу
        # P.S. минусы неудобно отделять сразу пробелами, т.к. есть отрицательные числа,
        # поэтому их обработаем отдельно вторым проходом
        for elem in "+*/%()":
            ans = ans.replace(elem, f" {elem} ")

        # Как будто бы самый подходящий момент для того, чтобы заменить буковки (известные)
        # на цифорки (мы же их сохранили в словаре)
        #print(ans)
        for el in findall(r'(?<=\[)[^\[\]]+(?=\])',ans):
            if el in dictionary:
                ans = ans.replace(el, dictionary[el])
        #print(ans)
        for el in findall(r'[a-zA-Z]+\[[^\]]+\]', ans):
            key = el[:el.find('[')]
            #print(key)
            if key not in arrays:
                print('ooo')
                return 'Runtime Error'
            val = el[el.find('[')+1:len(el)-1]
            val = float(CalculateExpression(val))
            #print(key, val)
            if str(val).isalpha():
                print('oooo', el)
                return 'Runtime error'
            
            ans = ans.replace(el, arrays[key][0][int(val)])
        for key in dictionary:
            ans = ans.replace(key, dictionary[key])
        #print(ans)
        # Находим импостеров - оставшиеся буквы, которые не смогли замениться
        # (А не заменились они потому что таких переменных нет в нашей памяти)
        # Поэтому вернем Runtime Error
        if (any(el.isalpha() for el in ans)):
            return "Runtime Error"

        # Наконец-то можно разделить строку по пробелам
        temp = ans.split(" ")
        #print(temp)
        #print('Temp:', temp)
        # Пришёл черёд минусов
        # Пора начать понимать то, где минус разделяет операнды, а где он выступает
        # в унарной роли (отрицательные числа)

        # Что в точности делает этот кусок кода я уже не помню, т.к. придумывал логику
        # месяца 2 назад... Но факт в том, что здесь 100% разделяются минусы
        
        for element in temp:
            if len(element) >= 3:
                if element[0] == "-":
                    it = 1
                    while it < len(element) and (element[it] not in "+-*/%"):
                        it += 1
                    spltd.append(element[:it])

                    for x in element[it:].replace("-", " - ").split(" "):
                        if (x != " " and x != ""):
                            spltd.append(x)
                else:
                    for x in element.replace("-", " - ").split(" "):
                        spltd.append(x)
                        
            else:
                if len(element) == 2 and element[-1] == "-":
                    spltd.append(element[0])
                    spltd.append(element[-1])
                else:
                    spltd.append(element)

        st = [] # Стэк
        #print(spltd)
        spltd = [element for element in spltd if element != ""]
        # Начинаем инфиксную запись переводить в постфиксную (польскую запись)
        for expr in spltd:
            if expr not in "+-*/%()":
                t = expr

                # Это тоже условие для калькулятора,
                # типа после разделения по операндам осталось "5," что тоже самое что "5"
                if expr[-1] == ",":
                    t = expr[:-1]

                # Зачем нам запятые, когда существуют точки?
                if "," in expr:
                    t = expr.replace(",", ".")
                
                postfix.append(t) # Если встретилось число, добавляем его в первый стек

            else:
                #print(postfix, st, expr)
                if expr == '(':
                    st.append(expr)
                elif expr == ')':
                    while st[-1] != '(':
                        postfix.append(st.pop())
                    if st[-1] == '(':
                        st.pop()
                else:
                    while (st and ((expr in "+-") and (st[-1] in "*/%") or\
                                   (expr in "+-") and (st[-1] in "+-") or\
                                   (expr in "*/%") and (st[-1] in "*/%"))):
                        postfix.append(st.pop())
                    st.append(expr)


        while len(st) > 0:
            postfix.append(st.pop())
        if postfix and postfix[-1] == "":
            postfix.pop()
        flag = True
        res = []

        # Польская нотация у нас уже есть, осталось посчитать ответ
        #print("Postfix", postfix)
        for elem in postfix:
            if elem not in "+-*/%":
                if elem == "-0" or float(elem) - 0.0 == 0.0:
                    res.append("0")
                else:
                    res.append(elem)
            else:
                f = res.pop()
                s = res.pop()

                if elem == "+":
                    res.append(str(float(f) + float(s)))

                if elem == "-":
                    res.append(str(float(s)-float(f)))
                
                if elem == "*":
                    res.append(str(float(f) * float(s)))

                if elem == "%":
                    if f == "0" or f == "-0":
                        GlobalResult = "Error"

                        flag = False
                        break

                    else:
                        res.append(str(float(s)%float(f)))

                if elem == "/":
                    if f == "0" or f == "-0":
                        GlobalResult = "Error"

                        flag = False
                        break
                    res.append(str(float(s)//float(f)))

        if flag:
            GlobalResult = res.pop().replace(",", ".")
        if peremen:
            if arrval:
                if arrkey in arrays:
                    arrval = int(float(CalculateExpression(arrval)))
                    
                    if not(str(arrval).isalpha()) and f'{arrkey}[{arrval}]' in arrays[arrkey][1]:
                        arrays[arrkey][0][int(arrval)] = GlobalResult
                        #print('Bad')
                    elif arrval.isalpha():
                        if arrval in dictionary:
                            #print(arrval)
                            arrval = arrval.replace(arrval, dictionary[arrval])
                            #print('Arr',arrval)
                            if not(arrval.isalpha()) and f'{arrkey}[{arrval}]' in arrays[arrkey][1]:
                                arrays[arrkey][0][int(arrval)] = GlobalResult
                                #print('Shiish')
            else:
                #print('Bad')
                dictionary[peremen] = GlobalResult
        return GlobalResult


code = []

s = input()

code.append(s)

while s:
    s = input()
    code.append(s)


ifs = conditionFinder(code)
cycles = cycleFinder(code)

print('Cycles', cycles)
print('Ifs', ifs)
iterator = 0

nextline = 1
code.pop()
#print(ifs)
while iterator < len(code):
    current = code[iterator]

    
    if ifs and ((iterator >= ifs[0][0][1] and (not(cycles) or cycles and iterator >= cycles[0][0][1])) or cycles and cycles[0][0][1] >= ifs[0][0][1] and iterator==cycles[0][0][1]):
        ifs.pop(0)
    if cycles and ((iterator >= cycles[0][0][1]) or len(cycles) >= 2 and cycles[1][0][0] == iterator):
        cycles.pop(0)

    if ifs and 'if' in current:
        
        getres = polandCondition(current)

        if getres:
            iterator = ifs[0][1][iterator][0]

        else:
            iterator = ifs[0][1][iterator][1]
    elif cycles and 'while' in current:
        getres = polandCondition(current)
 
        
        if getres:
            iterator = cycles[0][1][iterator][0]
  
        else:
            iterator = cycles[0][1][iterator][1]
  
    else:
        if fullmatch(r'print\(.+\)', current):
            show(current)
        elif fullmatch(r'[a-zA-Z]+(\s|)=(\s|)\[([a-zA-Z0-9]+;(\s|))*[0-9]+\]', current):
            processArray(current)
        else:
            CalculateExpression(current)
        if ifs and iterator in ifs[0][1] and (cycles and iterator not in cycles[0][1] or not(cycles)):
            iterator = ifs[0][1][iterator]
        elif cycles and iterator in cycles[0][1]:
            iterator = cycles[0][1][iterator]
        else:
            iterator += 1
    #else:
        #iterator += 1
    #print(dictionary)
    #print(iterator, dictionary, arrays)
            
print(dictionary)
print(arrays)

        



