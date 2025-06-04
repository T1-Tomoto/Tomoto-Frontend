package com.example.tomoto.structure.model

import kotlin.math.pow

object LevelConfig {
    const val baseXp = 100
    const val xpGrowthFactor = 1.2f

    fun xpThresholdFor(level: Int): Int {
        return (baseXp * xpGrowthFactor.pow(level - 1)).toInt()
    }
}