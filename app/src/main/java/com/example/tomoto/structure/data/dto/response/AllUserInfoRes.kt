package com.example.tomoto.structure.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class AllUserInfoRes(
    val userId: Long,
    val nickname: String,
    val level: Int
)
