package com.example.tomoto.structure.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class FriendsRankRes(
    val nickname: String,
    val pomoNum: Int
)
