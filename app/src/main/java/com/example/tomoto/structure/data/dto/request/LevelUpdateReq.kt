package com.example.tomoto.structure.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class LevelUpdateReq(
    val level: String,
    val xp: String,
)
