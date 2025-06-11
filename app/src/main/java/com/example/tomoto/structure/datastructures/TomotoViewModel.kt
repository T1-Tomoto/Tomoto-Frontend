package com.example.tomoto.structure.datastructures

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomato.UIcomponents.ToDoItem
import com.example.tomoto.structure.bottombarcontents.todolist.StudySession
import com.example.tomoto.structure.model.Challenge
import com.example.tomoto.structure.model.ChallengeListFactory
import com.example.tomoto.structure.model.UserLevelState
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

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
    // ToDo 리스트 데이터
    private val _allTasks = mutableStateListOf<ToDoItem>()
    val allTasks: SnapshotStateList<ToDoItem> get() = _allTasks

    private val _pomoRecords = mutableStateListOf<Pair<LocalDate, Int>>()

    val pomoCountData: State<Map<LocalDate, Int>> = derivedStateOf {
        _pomoRecords
            .groupBy { it.first } // 날짜(첫 번째 값)로 그룹화
            .mapValues { entry ->
                // 각 날짜에 해당하는 모든 횟수(두 번째 값)를 합산합니다.
                entry.value.sumOf { it.second }
            }
    }

    // 횟수를 직접 추가하는 새 함수
    fun addPomoCount(date: LocalDate, count: Int) {
        if (count > 0) {
            _pomoRecords.add(date to count)
        }
    }

    // --- 공부 시간 관련 로직 끝 ---

    private val dueDateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

    init {
        loadInitialTasks()

        // --- init 블록의 예시 데이터도 새 방식으로 변경 ---
        // 이제 훨씬 깔끔하게 예시 데이터를 추가할 수 있습니다.
        addPomoCount(LocalDate.of(2025, 5, 1), 5)
        addPomoCount(LocalDate.of(2025, 5, 2), 8)
        addPomoCount(LocalDate.of(2025, 5, 3), 3)
        addPomoCount(LocalDate.of(2025, 5, 4), 10)
        addPomoCount(LocalDate.of(2025, 5, 5), 2)
        addPomoCount(LocalDate.of(2025, 5, 10), 12)
        addPomoCount(LocalDate.of(2025, 5, 11), 7)
        addPomoCount(LocalDate.of(2025, 5, 23), 4)
        addPomoCount(LocalDate.of(2025, 5, 29), 10)
    }

    private fun loadInitialTasks() {
        // 기존 ToDoScreenWithCalendarComposable2에서 가져온 더미 데이터
        val initialTasks = listOf(
            ToDoItem(text = "5월 10일 디자인 검토", dueDate = "2025.05.10"),
            ToDoItem(text = "5월 10일 회의 준비", dueDate = "2025.05.10", isCompleted = true),
            ToDoItem(text = "5월 12일 보고서 작성", dueDate = "2025.05.12"),
            ToDoItem(text = "헬스장 가기", dueDate = LocalDate.of(2025,5,10).format(dueDateFormatter)) // 초기 선택된 날짜 기준
        )
        _allTasks.addAll(initialTasks.filterNot { task -> _allTasks.any { it.id == task.id } })
    }

    fun addTask(text: String, dueDate: LocalDate) {
        if (text.isNotBlank()) {
            val newToDo = ToDoItem(
                text = text,
                dueDate = dueDate.format(dueDateFormatter)
            )
            _allTasks.add(newToDo)
        }
    }

    fun updateTask(updatedTask: ToDoItem) {
        val index = _allTasks.indexOfFirst { it.id == updatedTask.id }
        if (index != -1) {
            _allTasks[index] = updatedTask
        }
    }

    fun deleteTask(taskToDelete: ToDoItem) {
        _allTasks.removeAll { it.id == taskToDelete.id }
    }
}
