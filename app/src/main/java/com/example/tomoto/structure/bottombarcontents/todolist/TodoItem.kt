package com.example.tomoto.structure.bottombarcontents.todolist

import java.util.UUID

data class ToDoItem(
    val id: String = UUID.randomUUID().toString(), // 고유 ID
    val text: String,
    var isCompleted: Boolean = false,
    val dueDate: String // 예: "2025.05.02"
)
