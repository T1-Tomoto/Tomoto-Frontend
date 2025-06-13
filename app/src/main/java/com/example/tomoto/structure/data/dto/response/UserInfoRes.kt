package com.example.tomoto.structure.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class UserInfoRes(
    val id:String,
    val nickname:String,
    val level: Int,
    val xp: Int,
    val totalPomo: Int,
    val challenges: List<Boolean>
)
