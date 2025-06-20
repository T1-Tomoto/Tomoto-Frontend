package com.example.tomoto.structure.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class MusicListRes(
    val musicId: Long,
    val url: String
)
