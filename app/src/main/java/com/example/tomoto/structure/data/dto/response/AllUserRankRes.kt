package com.example.tomoto.structure.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class AllUserRankRes(
    val userId: Long,
    val nickname: String,
    val level: Int,
    val xp: Int,
    val bio: String? = null
)
