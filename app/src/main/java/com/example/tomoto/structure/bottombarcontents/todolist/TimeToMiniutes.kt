package com.example.pomato.UIcomponents

fun parseStudyTimeToMinutes(studyTime: String?): Int {
    if (studyTime.isNullOrBlank()) return 0
    return try {
        val parts = studyTime.split(":")
        val hours = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val minutes = parts.getOrNull(1)?.toIntOrNull() ?: 0
        (hours * 60 + minutes).coerceAtLeast(0) // 음수 방지
    } catch (e: Exception) {
        // 파싱 오류 시 0분으로 처리
        0
    }
}