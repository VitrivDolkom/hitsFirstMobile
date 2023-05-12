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
    BRACES("()"), DEFAULT("");
    
    private fun mathOperations(): List<CodeBlockOperation> =
        listOf(ADD, SUBTRACT, MULTIPLY, DIVIDE, PERCENT, BRACES, INPUT)
    
    fun isMathOperation(): Boolean = this in mathOperations()
    
    private fun specialOperations(): List<CodeBlockOperation> =
        listOf(LOOP, CONDITION, DEFAULT)
    
    fun isSpecialOperation(): Boolean =
        this in specialOperations() && this != DEFAULT
    
    fun getVariants(): List<CodeBlockOperation> =
        if (this in mathOperations()) mathOperations() else specialOperations()
    
    fun blocksList(): List<CodeBlocksGroup> {
        return listOf(
            CodeBlocksGroup("Переменные", listOf(EQUAL), listOf(EQUAL)),
            CodeBlocksGroup("Массивы", listOf(ARRAY_EQUAL), listOf(ARRAY_EQUAL)),
            CodeBlocksGroup("Мат операции", mathOperations(), listOf(ADD)),
            CodeBlocksGroup(
                "Спец операции", specialOperations(), listOf(LOOP, CONDITION)
            ),
        )
    }
}