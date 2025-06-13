package com.example.tomoto.structure.data.dto.request

import com.example.tomoto.structure.util.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class AddTodoReq(
    @Serializable(with = LocalDateTimeSerializer::class)
    val dueDate: LocalDateTime,
    val content: String
)