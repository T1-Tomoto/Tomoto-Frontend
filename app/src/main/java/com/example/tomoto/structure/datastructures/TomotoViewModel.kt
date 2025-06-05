package com.example.tomoto.structure.datastructures

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tomoto.structure.model.Challenge
import com.example.tomoto.structure.model.ChallengeListFactory
import com.example.tomoto.structure.model.UserLevelState
import kotlinx.coroutines.launch

class TomotoViewModel : ViewModel() {

    val userName = "UserName"
    val userEmail = "tomoto@gmail.com"
    val totalPomodoro = 2

//music 관련 데이터
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

//challenge 관련 데이터
    var dailyChallenges = mutableStateListOf<Challenge>()
        private set

    var permanentChallenges = mutableStateListOf<Challenge>()
        private set

    //영구 도전과제 리스트 - 데이터베이스에서 불러와 초기화 필요
    private val permanentChallengeStates = listOf(
        false, false, false, false, false,
        false, false, false, false, false, false, false, false
    )

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
            val savedDailyStates = ChallengePrefs.loadDailyStates(context)

            if (ChallengePrefs.shouldResetDaily(context)) {
                dailyChallenges.clear()
                val freshList = ChallengeListFactory.getDailyChallenges()
                dailyChallenges.addAll(freshList)
                ChallengePrefs.updateResetDate(context)
                ChallengePrefs.saveDailyStates(context, List(freshList.size) { false })
            } else {
                loadChallenges(
                    dailyStates = savedDailyStates,
                    permanentStates = permanentChallengeStates
                )
            }

            if (permanentChallenges.isEmpty()) {
                loadChallenges(permanentStates = permanentChallengeStates)
            }
        }
    }

    var userLevel = UserLevelState()
        private set

    fun initializeUserLevelFromDb(level: Int, xp: Int) {
        userLevel = UserLevelState.fromDatabase(level, xp)
    }

    fun evaluateDailyChallenges(context: Context, pomodoroCount: Int) {
        ChallengeManager.checkDailyChallengesAfterTimer(
            context = context,
            scope = viewModelScope,
            dailyChallenges = dailyChallenges,
            userLevel = userLevel,
            pomodoroCount = pomodoroCount,
            onXpGained = { updated -> userLevel = updated }
        )
    }

    fun evaluatePermanentChallenges(pomodoroTotal: Int, timerStreak: Int, totalCompleted: Int) {
        ChallengeManager.checkPermanentChallenges(
            permanentChallenges = permanentChallenges,
            userLevel = userLevel,
            onXpGained = { updated -> userLevel = updated },
            pomodoroTotal = pomodoroTotal,
            timerStreak = timerStreak,
            totalCompleted = totalCompleted
        )
    }
}
