package com.example.tomoto.structure.datastructures

import UserLevelState
import android.content.Context
import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewModelScope
import com.example.tomoto.structure.data.service.ServicePool
import com.example.tomoto.structure.model.Challenge
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Calendar

object ChallengeManager {

    fun checkDailyChallengesAfterTimer(
        context: Context,
        scope: CoroutineScope,
        dailyChallenges: SnapshotStateList<Challenge>,
        userLevel: UserLevelState,
        pomodoroCount: Int,
        timerStreak: Int,
        viewModel: TomotoViewModel
    ) {
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        dailyChallenges.forEachIndexed { index, challenge ->
            if (challenge.isCompleted) return@forEachIndexed

            val shouldComplete = when (challenge.title) {
                "아침형 인간" -> checkMorningPerson(currentHour)
                "하루 한 번의 집중" -> checkOnceFocus()
                "뽀모도로 입문자" -> checkPomodoroBeginner(pomodoroCount)
                "뽀모도로 실천가" -> checkPomodoroPractitioner(pomodoroCount)
                "뽀모도로 숙련자" -> checkPomodoroSkilled(pomodoroCount)
                "뽀모도로 마스터" -> checkPomodoroMaster(pomodoroCount)
                "달콤한 휴식" -> checkSweetRest(timerStreak)
                else -> false
            }

            if (shouldComplete) {
                val xp = getDailyChallengeXp(challenge.title)
                completeChallenge(context, scope, dailyChallenges, index, xp, true, viewModel)
            }
        }
    }

    fun checkPermanentChallenges(
        scope: CoroutineScope,
        permanentChallenges: SnapshotStateList<Challenge>,
        pomodoroTotal: Int,
        timerStreak: Int,
        totalCompleted: Int,
        viewModel: TomotoViewModel
    ) {
        var updated = false

        permanentChallenges.forEachIndexed { index, challenge ->
            if (challenge.isCompleted) return@forEachIndexed

            val shouldComplete = when (challenge.title) {
                "연속 집중 2단계" -> timerStreak >= 2
                "연속 집중 4단계" -> timerStreak >= 4
                "연속 집중 6단계" -> timerStreak >= 6
                "뽀모도로 10회 완주" -> pomodoroTotal >= 10
                "뽀모도로 20회 완주" -> pomodoroTotal >= 20
                "뽀모도로 30회 완주" -> pomodoroTotal >= 30
                "뽀모도로 40회 완주" -> pomodoroTotal >= 40
                "뽀모도로 50회 완주" -> pomodoroTotal >= 50
                "뽀모도로 100회 정복" -> pomodoroTotal >= 100
                "도전 과제 입문자" -> totalCompleted >= 5
                "도전 과제 정복자" -> totalCompleted >= 10
                else -> false
            }

            if (shouldComplete) {
                val xp = getPermanentChallengeXp(challenge.title)
                completeChallenge(null, null, permanentChallenges, index, xp, false, viewModel)
                updated = true
            }
        }

        if (updated) {
            val completedStates = permanentChallenges.map { it.isCompleted }
            scope.launch {
                Log.i("ChallengeManager", "완료된 챌린지 상태: $completedStates")
                ServicePool.userService.challengeUpdate(completedStates)
            }
        }
    }

    private fun checkMorningPerson(currentHour: Int): Boolean = currentHour < 10
    private fun checkOnceFocus(): Boolean = true
    private fun checkPomodoroBeginner(pomo: Int) = pomo >= 2
    private fun checkPomodoroPractitioner(pomo: Int) = pomo >= 4
    private fun checkPomodoroSkilled(pomo: Int) = pomo >= 6
    private fun checkPomodoroMaster(pomo: Int) = pomo >= 8
    private fun checkSweetRest(timerStreak: Int) = timerStreak == 4

    private fun getDailyChallengeXp(title: String): Int = when (title) {
        "아침형 인간" -> 30
        "하루 한 번의 집중" -> 30
        "뽀모도로 입문자" -> 50
        "뽀모도로 실천가" -> 70
        "뽀모도로 숙련자" -> 90
        "뽀모도로 마스터" -> 120
        "달콤한 휴식" -> 40
        else -> 0
    }

    private fun getPermanentChallengeXp(title: String): Int = when (title) {
        "연속 집중 2단계" -> 50
        "연속 집중 4단계" -> 80
        "연속 집중 6단계" -> 120
        "뽀모도로 10회 완주" -> 50
        "뽀모도로 20회 완주" -> 70
        "뽀모도로 30회 완주" -> 90
        "뽀모도로 40회 완주" -> 110
        "뽀모도로 50회 완주" -> 140
        "뽀모도로 100회 정복" -> 200
        "도전 과제 입문자" -> 100
        "도전 과제 정복자" -> 200
        else -> 0
    }

    fun completeChallenge(
        context: Context?,
        scope: CoroutineScope?,
        challenges: SnapshotStateList<Challenge>,
        index: Int,
        xp: Int,
        isDaily: Boolean,
        viewModel: TomotoViewModel
    ) {
        if (index !in challenges.indices) return
        val challenge = challenges[index]
        if (challenge.isCompleted) return

        challenges[index] = challenge.copy(isCompleted = true)
        viewModel.gainXp(xp)

        if (isDaily && context != null && scope != null) {
            scope.launch {
                ChallengePrefs.saveDailyStates(
                    context,
                    challenges.map { it.isCompleted }
                )
            }
        }
    }
}
