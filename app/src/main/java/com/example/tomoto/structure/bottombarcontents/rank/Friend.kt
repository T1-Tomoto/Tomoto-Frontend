package com.example.tomoto.structure.bottombarcontents.rank

data class Friend(
    val id: Int,
    val nickname: String,
    val isActive: Boolean,
    val pomodoroCount: Int,
    val level: Int,
    val imageRes: Int
)
