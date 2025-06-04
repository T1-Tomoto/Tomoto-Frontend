package com.example.tomoto.structure.model

// model/UserLevelState.kt
data class UserLevelState(
    val level: Int = 1,
    val xp: Int = 0,
    val xpForNextLevel: Int = LevelConfig.baseXp
)

