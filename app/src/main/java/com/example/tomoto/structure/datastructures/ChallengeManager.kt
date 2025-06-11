package com.example.tomoto.structure.datastructures

import UserLevelState
import android.content.Context
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.tomoto.structure.model.Challenge
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object ChallengeManager{

    //타이머 이후에 호출해서 검사
    fun checkDailyChallengesAfterTimer(
        context: Context,
        scope: CoroutineScope,
        dailyChallenges: SnapshotStateList<Challenge>,
        userLevel: UserLevelState,
        pomodoroCount: Int,
        viewModel: TomotoViewModel
    ) {
        dailyChallenges.forEachIndexed { index, challenge ->
            if (challenge.isCompleted) return@forEachIndexed

            when (challenge.title) {
                "하루 한 번의 집중" -> completeChallenge(context, scope, dailyChallenges, index, 30, true, viewModel)
                "뽀모도로 입문자" -> if (pomodoroCount >= 2) completeChallenge(context, scope, dailyChallenges, index, 50, true, viewModel)
                "뽀모도로 실천가" -> if (pomodoroCount >= 4) completeChallenge(context, scope, dailyChallenges, index, 70, true, viewModel)
                "뽀모도로 숙련자" -> if (pomodoroCount >= 6) completeChallenge(context, scope, dailyChallenges, index, 90, true, viewModel)
                "뽀모도로 마스터" -> if (pomodoroCount >= 8) completeChallenge(context, scope, dailyChallenges, index, 120, true, viewModel)
            }
        }
    }

    //타이머 이후에 호출해 검사
    fun checkPermanentChallenges(
        permanentChallenges: SnapshotStateList<Challenge>,
        userLevel: UserLevelState,
        pomodoroTotal: Int,
        timerStreak: Int,
        totalCompleted: Int,
        viewModel: TomotoViewModel
    ) {
        permanentChallenges.forEachIndexed { index, challenge ->
            if (challenge.isCompleted) return@forEachIndexed

            when (challenge.title) {
                "연속 집중 2단계" -> if (timerStreak >= 2) completeChallenge(null, null, permanentChallenges, index, 50, false, viewModel)
                "연속 집중 4단계" -> if (timerStreak >= 4) completeChallenge(null, null, permanentChallenges, index, 80, false, viewModel)
                "연속 집중 6단계" -> if (timerStreak >= 6) completeChallenge(null, null, permanentChallenges, index, 120, false, viewModel)
                "뽀모도로 10회 완주" -> if (pomodoroTotal >= 10) completeChallenge(null, null, permanentChallenges, index, 50, false, viewModel)
                "뽀모도로 20회 완주" -> if (pomodoroTotal >= 20) completeChallenge(null, null, permanentChallenges, index, 70, false, viewModel)
                "뽀모도로 30회 완주" -> if (pomodoroTotal >= 30) completeChallenge(null, null, permanentChallenges, index, 90, false, viewModel)
                "뽀모도로 40회 완주" -> if (pomodoroTotal >= 40) completeChallenge(null, null, permanentChallenges, index, 110, false, viewModel)
                "뽀모도로 50회 완주" -> if (pomodoroTotal >= 50) completeChallenge(null, null, permanentChallenges, index, 140, false, viewModel)
                "뽀모도로 100회 정복" -> if (pomodoroTotal >= 100) completeChallenge(null, null, permanentChallenges, index, 200, false, viewModel)
                "도전 과제 입문자" -> if (totalCompleted >= 5) completeChallenge(null, null, permanentChallenges, index, 100, false, viewModel)
                "도전 과제 정복자" -> if (totalCompleted >= 10) completeChallenge(null, null, permanentChallenges, index, 200, false, viewModel)
            }
        }
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