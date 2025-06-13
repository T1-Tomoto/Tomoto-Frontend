package com.example.tomoto.structure.data.dto.response

import com.example.tomoto.structure.util.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class DailyPomoCountDto(
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    val pomoNum: Int
)
