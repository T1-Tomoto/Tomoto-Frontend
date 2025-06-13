package com.example.tomoto.structure.data.dto.response

import com.example.tomoto.structure.util.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class AllTodoRes(
    val todoId: Long,
    @Serializable(with = LocalDateTimeSerializer::class)
    val dueDate: LocalDateTime?,
    val content: String,
    val completed: Boolean
)
