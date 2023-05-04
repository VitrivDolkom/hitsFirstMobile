package com.example.firstmobile.views

data class CodeBlock(val leftBlock: CodeBlock? = null, val operation: String = "", val rightBlock: CodeBlock? = null)