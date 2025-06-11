package com.example.tomoto.structure.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UserLoginReq(
    val id: String,
    val password: String,
)
