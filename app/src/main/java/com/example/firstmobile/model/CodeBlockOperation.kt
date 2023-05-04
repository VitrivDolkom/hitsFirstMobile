package com.example.firstmobile.model

enum class CodeBlockOperation(val symbol: String, val value: String = "") {
    ADD("+"), SUBTRACT("-"), MULTIPLY("*"), DIVIDE("/"), PERCENT("%"),
    LOOP("while"), CONDITION("if"), INPUT_NUMBER("input", ""),
    VAR_A("a"), VAR_B("b"), VAR_C("c"), VAR_D("d"), VAR_E("e"), VAR_F("f"), VAR_G("g"), VAR_H("h"), VAR_J("j"),
    DEFAULT("");
    
    private fun mathOperations(): List<CodeBlockOperation> = listOf(ADD, SUBTRACT, MULTIPLY, DIVIDE, PERCENT)
    
    private fun variables(): List<CodeBlockOperation> = listOf(VAR_A, VAR_B, VAR_C, VAR_D, VAR_E, VAR_F, VAR_G, VAR_H, VAR_J)
    
    private fun specialOperations(): List<CodeBlockOperation> = listOf(LOOP, CONDITION, INPUT_NUMBER)
    
    fun blocksList(): List<List<CodeBlockOperation>> {
        return listOf(variables(), mathOperations(), specialOperations())
    }
}