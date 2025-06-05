package com.example.pomato.UIcomponents

import java.time.LocalDate

data class DayStudyData(
    val dayOfMonth: Int,
    val date: LocalDate,
    val studyTime: String?, // 예: "03:45"
    val isCurrentMonth: Boolean,
    val isSelected: Boolean = false
)