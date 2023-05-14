package com.example.firstmobile.model

data class CodeBlocksGroup(
    val name: String,
    val list: List<CodeBlockOperation>,
    val listToShow: List<CodeBlockOperation>
)

enum class CodeBlockOperation(val symbol: String, val value: String = "") {
    EQUAL("="), ARRAY_EQUAL("="), ADD("+"), SUBTRACT("-"), MULTIPLY("*"), DIVIDE(
        "/"
    ),
    PERCENT("%"), LOOP("while"), CONDITION(
        "if"
    ),
    INPUT(
        ""
    ),
    BRACES("( )"), MORE(">"), LESS("<"), MORE_EQUAL(">="), LESS_EQUAL("<="), COMPARE_EQUAL(
        "=="
    ),
    LOGIC_AND("and"), LOGIC_OR("or"), PRINT("print"), BLOCK_END("end"), DEFAULT(
        ""
    );
    
    private fun mathOperations(): List<CodeBlockOperation> =
        listOf(ADD, SUBTRACT, MULTIPLY, DIVIDE, PERCENT, BRACES, INPUT)
    
    private fun compareOperations(): List<CodeBlockOperation> =
        listOf(COMPARE_EQUAL, MORE, LESS, MORE_EQUAL, LESS_EQUAL, BRACES, INPUT)
    
    private fun logicOperations(): List<CodeBlockOperation> =
        listOf(LOGIC_AND, LOGIC_OR, BRACES, INPUT)
    
    private fun specialOperations(): List<CodeBlockOperation> =
        listOf(LOOP, CONDITION, PRINT, DEFAULT)
    
    private fun emptyBlocks(): List<CodeBlockOperation> = listOf(BLOCK_END)
    
    fun isSpecialOperation(): Boolean =
        this in specialOperations() && this != DEFAULT
    
    fun isEmptyBlock(): Boolean = this in emptyBlocks()
    
    private fun isMathOperation(): Boolean = this in mathOperations()
    
    fun isLogicOperation(): Boolean = this in logicOperations()
    
    private fun isCompareOperation(): Boolean = this in compareOperations()
    
    fun isDropDownable(): Boolean =
        isCompareOperation() || isLogicOperation() || isMathOperation()
    
    fun getVariants(): List<CodeBlockOperation> {
        if (this in mathOperations()) return mathOperations()
        else if (this in logicOperations()) return logicOperations()
        return compareOperations()
    }
    
    fun blocksList(): List<CodeBlocksGroup> {
        return listOf(
            CodeBlocksGroup("Переменные", listOf(EQUAL), listOf(EQUAL)),
            CodeBlocksGroup(
                "Массивы", listOf(ARRAY_EQUAL), listOf(ARRAY_EQUAL)
            ),
            CodeBlocksGroup("Мат операции", mathOperations(), listOf(ADD)),
            CodeBlocksGroup(
                "Логические операции", logicOperations(), listOf(LOGIC_AND)
            ),
            CodeBlocksGroup(
                "Операции сравнения", compareOperations(), listOf(MORE)
            ),
            CodeBlocksGroup(
                "Спец операции",
                specialOperations(),
                listOf(LOOP, CONDITION, PRINT)
            ),
            CodeBlocksGroup("Блок окончания", emptyBlocks(), emptyBlocks())
        )
    }
}