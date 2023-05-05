package com.example.firstmobile.views.draganddrop

import java.util.UUID

data class CodeBlock(var leftBlock: CodeBlock? = null, var operation: String = "", var rightBlock: CodeBlock? = null, val id: UUID = UUID.randomUUID())