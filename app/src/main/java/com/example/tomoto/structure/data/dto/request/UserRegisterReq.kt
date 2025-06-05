package com.example.tomoto.structure.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UserRegisterReq(
    val id: String,
    val password: String,
    val nickname: String
)
