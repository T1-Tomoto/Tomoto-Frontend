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
    //db 필요
    val userName = "UserName"
    val userEmail = "tomoto@gmail.com"
    val totalPomodoro = 2

//music 관련 데이터
    //db 필요
var musicList = mutableStateListOf<String>()
    private set

    fun addMusicUrl(url: String) {
        MusicManager.addMusicUrl(musicList, url)
    }

    fun removeMusicUrl(url: String) {
        MusicManager.removeMusicUrl(musicList, url)
    }

    fun editMusicUrl(oldUrl: String, newUrl: String) {
        MusicManager.editMusicUrl(musicList, oldUrl, newUrl)
    }

//challenge 관련 데이터
    var dailyChallenges = mutableStateListOf<Challenge>()
        private set

    var permanentChallenges = mutableStateListOf<Challenge>()
        private set

    //영구 도전과제 리스트
    //DB 필요
    private val permanentChallengeStates = listOf(
        false, false, false, false, false,
        false, false, false, false, false, false, false, false
    )

    private fun loadDailyChallenges(dailyStates: List<Boolean>) {
        val daily = ChallengeListFactory.getDailyChallenges()
        dailyChallenges.addAll(
            daily.mapIndexed { index, challenge ->
                challenge.copy(isCompleted = dailyStates.getOrNull(index) ?: false)
            }
        )
    }

    private fun loadPermanentChallenges(permanentStates: List<Boolean>) {
        val permanent = ChallengeListFactory.getPermanentChallenges()
        permanentChallenges.addAll(
            permanent.mapIndexed { index, challenge ->
                challenge.copy(isCompleted = permanentStates.getOrNull(index) ?: false)
            }
        )
    }


    fun initializeChallenges(context: Context) {
        viewModelScope.launch {
            val savedDailyStates = ChallengePrefs.loadDailyStates(context)

            if (ChallengePrefs.shouldResetDaily(context)) {
                dailyChallenges.clear()
                val freshList = ChallengeListFactory.getDailyChallenges()
                dailyChallenges.addAll(freshList)
                ChallengePrefs.updateResetDate(context)
                ChallengePrefs.saveDailyStates(context, List(freshList.size) { false })
            } else {
                loadDailyChallenges(savedDailyStates)
            }

            if (permanentChallenges.isEmpty()) {
                loadPermanentChallenges(permanentChallengeStates)
            }
        }
    }

//유저 레벨 정보
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
