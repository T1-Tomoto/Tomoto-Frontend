package com.example.tomoto.structure.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tomoto.structure.model.Challenge
import com.example.tomoto.structure.model.ChallengeListFactory
import com.example.tomoto.structure.model.LevelConfig
import com.example.tomoto.structure.model.UserLevelState
import kotlinx.coroutines.launch

class TomotoViewModel:ViewModel() {
    
    val UserName = "UserName"
    val UserEmail = "tomoto@gmail.com"

    var musicList = mutableStateListOf<String>()
        private set
    fun addMusicUrl(url: String) {
        if (url.isNotBlank()) {
            musicList.add(url.trim())
        }
    }
    fun removeMusicUrl(url: String) {
        musicList.remove(url)
    }
    fun editMusicUrl(oldUrl: String, newUrl: String) {
        val index = musicList.indexOf(oldUrl)
        if (index != -1 && newUrl.isNotBlank()) {
            musicList[index] = newUrl.trim()
        }
    }

    val challengeList = mutableStateListOf("challenge1", "challenge2", "challenge3")

    // 내부적으로 초기화 로직 추가했으니 DB에는 필요 x
    private val dailyChallengeStates = listOf(
        false, false, false, true, false, false, false
    )
    //데이터베이스에서 받아오기
    private val permanentChallengeStates = listOf(
        true, true, false, false, false,
        true, false, false, false, false, false, true, false
    )

    var dailyChallenges = mutableStateListOf<Challenge>()
        private set

    var permanentChallenges = mutableStateListOf<Challenge>()
        private set

    private fun loadChallenges(
        dailyStates: List<Boolean> = emptyList(),
        permanentStates: List<Boolean> = emptyList()
    ) {
        val daily = ChallengeListFactory.getDailyChallenges()
        val permanent = ChallengeListFactory.getPermanentChallenges()

        dailyChallenges.addAll(
            daily.mapIndexed { index, challenge ->
                challenge.copy(isCompleted = dailyStates.getOrNull(index) ?: false)
            }
        )

        permanentChallenges.addAll(
            permanent.mapIndexed { index, challenge ->
                challenge.copy(isCompleted = permanentStates.getOrNull(index) ?: false)
            }
        )
    }

    fun initialize(context: Context) {
        viewModelScope.launch {
            if (ChallengePrefs.shouldResetDaily(context)) {
                dailyChallenges.clear()
                dailyChallenges.addAll(ChallengeListFactory.getDailyChallenges())
                ChallengePrefs.updateResetDate(context)
            }

            permanentChallenges.clear()
            loadChallenges( // 여기에 permanent 상태만 전달
                permanentStates = permanentChallengeStates
            )
        }
    }

    var userLevel = UserLevelState()
        private set

    fun gainXp(amount: Int) {
        var newXp = userLevel.xp + amount
        var level = userLevel.level
        var threshold = userLevel.xpForNextLevel

        while (newXp >= threshold) {
            newXp -= threshold
            level++
            threshold = LevelConfig.xpThresholdFor(level)
        }

        userLevel = UserLevelState(level, newXp, threshold)
    }
}