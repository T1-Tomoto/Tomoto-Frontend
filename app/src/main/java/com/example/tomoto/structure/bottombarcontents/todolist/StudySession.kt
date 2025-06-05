package com.example.tomoto.structure.bottombarcontents.todolist

import java.time.LocalDate
import java.util.UUID

data class StudySession(
    val id: String = UUID.randomUUID().toString(),
    val date: LocalDate,
    val durationInSeconds: Long // 공부 시간 (초 단위)
)
