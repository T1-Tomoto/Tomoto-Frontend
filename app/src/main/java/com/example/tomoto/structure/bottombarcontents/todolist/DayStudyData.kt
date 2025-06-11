package com.example.tomoto.structure.bottombarcontents.todolist

import java.time.LocalDate

data class DayStudyData(
    val dayOfMonth: Int,
    val date: LocalDate,
    val pomoCount: Int?,
    val isCurrentMonth: Boolean,
    val isSelected: Boolean = false
)