package com.example.firstmobile.views.draganddrop

import com.example.firstmobile.model.CodeBlockOperation
import java.util.UUID

data class CodeBlock(var leftBlock: CodeBlock? = null, var operation: CodeBlockOperation = CodeBlockOperation.DEFAULT, var rightBlock: CodeBlock? = null, val id: UUID = UUID.randomUUID())