package com.example.tomoto.structure.model

data class UserLevelState(
    val level: Int = 1,
    val xp: Int = 0,
    val xpForNextLevel: Int = LevelConfig.baseXp
) {
    companion object {
        fun fromDatabase(level: Int, xp: Int): UserLevelState {
            val threshold = LevelConfig.xpThresholdFor(level)
            return UserLevelState(level, xp, threshold)
        }
    }
}

