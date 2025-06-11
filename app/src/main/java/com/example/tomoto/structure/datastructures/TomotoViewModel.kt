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
    var todayPomodoro = 0
    var introduce = "default introduce text1"

    fun updateIntroduce(newIntroduce : String){
        introduce = newIntroduce
    }

    fun incrementPomodoroAndEvaluate(context: Context) {
        val newToday = todayPomodoro + 1
        todayPomodoro = newToday

        // 저장
        viewModelScope.launch {
            ChallengePrefs.saveTodayPomodoro(context, newToday)
        }

        // 도전 과제 평가
        evaluateDailyChallenges(context, pomodoroCount = newToday)
        evaluatePermanentChallenges(
            pomodoroTotal = totalPomodoro, // 여기는 total 기준으로 나중에 바뀔 예정
            timerStreak = 0,
            totalCompleted = dailyChallenges.count { it.isCompleted } + permanentChallenges.count { it.isCompleted }
        )

        // TODO: 서버에 totalPomodoro 1 증가 요청 API 호출 예정
    }

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

                todayPomodoro = 0
                ChallengePrefs.resetTodayPomodoro(context)
            } else {
                loadDailyChallenges(savedDailyStates)
                todayPomodoro = ChallengePrefs.loadTodayPomodoro(context) // 오늘값 불러오기
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

    // --- 공부 시간 관련 로직 ---
    private val _studySessions = mutableStateListOf<StudySession>()
    // val studySessions: SnapshotStateList<StudySession> get() = _studySessions // 필요시 공개

    val studyTimeData: State<Map<LocalDate, String>> = derivedStateOf {
        _studySessions
            .groupBy { it.date }
            .mapValues { entry ->
                val totalSeconds = entry.value.sumOf { it.durationInSeconds }
                formatSecondsToHHMM(totalSeconds)
            }
    }

    fun addStudySession(date: LocalDate, durationInSeconds: Long) {
        if (durationInSeconds > 0) {
            _studySessions.add(StudySession(date = date, durationInSeconds = durationInSeconds))

        }
    }

    private fun formatSecondsToHHMM(totalSeconds: Long): String {
        val hours = TimeUnit.SECONDS.toHours(totalSeconds)
        val minutes = TimeUnit.SECONDS.toMinutes(totalSeconds) % 60
        return String.format("%02d:%02d", hours, minutes)
    }
    // --- 공부 시간 관련 로직 끝 ---

    private val dueDateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

    init {
        loadInitialTasks()
        // 테스트용 더미 공부 세션 데이터 (실제로는 타이머에서 추가)
        // addStudySession(LocalDate.now().minusDays(2), 3600) // 예: 2일 전 1시간 공부
        // addStudySession(LocalDate.now().minusDays(1), 1800) // 예: 어제 30분 공부
        // addStudySession(LocalDate.now(), 5400)           // 예: 오늘 1시간 30분 공부

        // ToDoScreenWithCalendarComposable2에서 가져온 더미 데이터 예시 적용
        addStudySession(LocalDate.of(2025, 5, 1), (6 * 3600 + 44 * 60).toLong())
        addStudySession(LocalDate.of(2025, 5, 2), (7 * 3600 + 11 * 60).toLong())
        addStudySession(LocalDate.of(2025, 5, 3), (4 * 3600 + 59 * 60).toLong())
        addStudySession(LocalDate.of(2025, 5, 4), (3 * 3600 + 8 * 60).toLong())
        addStudySession(LocalDate.of(2025, 5, 5), (5 * 3600 + 11 * 60).toLong())
        // ... 나머지 studyTimeData에 해당하는 날짜와 시간도 이런 식으로 추가 ...
        addStudySession(LocalDate.of(2025, 5, 28), (3 * 3600 + 31 * 60).toLong())
        addStudySession(LocalDate.of(2025, 5, 31), (4 * 3600 + 32 * 60).toLong())

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
