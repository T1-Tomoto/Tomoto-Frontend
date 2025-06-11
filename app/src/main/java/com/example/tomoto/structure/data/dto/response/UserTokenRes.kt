package com.example.tomoto.structure.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class UserTokenRes(
    val accessToken: String
)
