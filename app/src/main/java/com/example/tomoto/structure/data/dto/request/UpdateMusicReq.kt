package com.example.tomoto.structure.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateMusicReq(
    val oldUrl: String,
    val newUrl: String
)
