import com.example.tomoto.structure.model.LevelConfig

data class UserLevelState(
    val level: Int = 1,
    val xp: Int = 0,
    val xpForNextLevel: Int = LevelConfig.xpThresholdFor(1)
) {
    val progressFraction: Float
        get() = if (xpForNextLevel > 0) xp.toFloat() / xpForNextLevel else 0f

    companion object {
        fun fromDatabase(level: Int, xp: Int): UserLevelState {
            val threshold = LevelConfig.xpThresholdFor(level)
            val clampedXp = xp.coerceAtMost(threshold)
            return UserLevelState(level, clampedXp, threshold)
        }
    }
}