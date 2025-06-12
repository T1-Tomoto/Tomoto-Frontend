package com.example.tomoto.structure.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class AddTodoReq(
    val dueDate: String,
    val content: String
)