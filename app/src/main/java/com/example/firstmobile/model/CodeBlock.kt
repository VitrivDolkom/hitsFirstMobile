package com.example.firstmobile.model

import java.util.UUID

data class CodeBlock(
    var leftBlock: CodeBlock? = null,
    var operation: CodeBlockOperation = CodeBlockOperation.DEFAULT,
    var rightBlock: CodeBlock? = null,
    val id: UUID = UUID.randomUUID(),
    var input: String = "",
    var leftBrace: Braces = Braces.DEFAULT,
    var rightBrace: Braces = Braces.DEFAULT
)

enum class Braces(val symbol: String) {
    OPEN_PARENTHESES("("), OPEN_SQUARE("["), CLOSE_PARENTHESES(")"), CLOSE_SQUARE(
        "]"
    ), DEFAULT("");
}
