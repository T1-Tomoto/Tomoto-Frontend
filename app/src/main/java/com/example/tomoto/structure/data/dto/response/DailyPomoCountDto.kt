package com.example.tomoto.structure.data.dto.response

import java.time.LocalDateTime

data class DailyPomoCountDto(
    val createdAt: LocalDateTime,
    val pomoNum: Int
)
