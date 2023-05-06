package com.example.firstmobile.model

enum class CodeBlockOperation(val symbol: String, val value: String = "") {
    EQUAL("="), ADD("+"), SUBTRACT("-"), MULTIPLY("*"), DIVIDE("/"), PERCENT("%"),
    LOOP("while"), CONDITION("if"),
    DEFAULT("");
    
    private fun mathOperations(): List<CodeBlockOperation> = listOf(EQUAL, ADD, SUBTRACT, MULTIPLY, DIVIDE, PERCENT)
    
    private fun variables(): List<CodeBlockOperation> = listOf(DEFAULT)
    
    private fun arrayVariables(): List<CodeBlockOperation> = listOf(DEFAULT)
    
    private fun specialOperations(): List<CodeBlockOperation> = listOf(LOOP, CONDITION)
    
    fun isSpecialOperation(): Boolean = this in specialOperations()
    
    fun isInput(): Boolean = this in variables() || this in arrayVariables()
    
    fun blocksList(): List<List<CodeBlockOperation>> {
        return listOf(variables(), mathOperations(), specialOperations())
    }
}