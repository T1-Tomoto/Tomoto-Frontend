package com.example.tomoto.structure.datastructures

import UserLevelState
import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tomoto.R
import com.example.tomoto.structure.bottombarcontents.rank.Friend
import com.example.tomoto.structure.bottombarcontents.todolist.ToDoItem
import com.example.tomoto.structure.data.dto.response.AllTodoRes
import com.example.tomoto.structure.data.dto.response.FriendsRankRes
import com.example.tomoto.structure.data.dto.response.UserInfoRes
import com.example.tomoto.structure.data.service.ServicePool
import com.example.tomoto.structure.model.Challenge
import com.example.tomoto.structure.model.ChallengeListFactory
import com.example.tomoto.structure.model.LevelConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TomotoViewModel : ViewModel() {

    private val _userInfo = MutableStateFlow<UserInfoRes?>(null)
    val userInfo: StateFlow<UserInfoRes?> = _userInfo

    fun fetchUserInfo() {
        viewModelScope.launch {
            try {
                val info = ServicePool.userService.info()
                Log.i("유저 정보", info.toString())
                _userInfo.value = info
            } catch (e: Exception) {
                Log.e("유저 정보", "에러: ${e.message}")
            }
        }
    }

    private val _todayPomodoro = MutableStateFlow(0)
    val todayPomodoro: StateFlow<Int> = _todayPomodoro

    fun fetchTodayPomodoro() {
        viewModelScope.launch {
            try {
                val todayPomo = ServicePool.pomoService.getTodayPomo()
                Log.i("TodayPomo", "오늘 뽀모도로: $todayPomo")
                _todayPomodoro.value = todayPomo
            } catch (e: Exception) {
                Log.e("TodayPomo", "에러: ${e.message}")
            }
        }
    }

    //todo 리스트
    private val _todoList = MutableStateFlow<List<AllTodoRes>>(emptyList())
    val todoList: StateFlow<List<AllTodoRes>> = _todoList

    fun fetchAllTodoList() {
        viewModelScope.launch {
            try {
                //TODO: 받아온 투두리스트 정보 처리
                val todoList = ServicePool.todoService.getAllTodo()
                Log.i("투두리스트",todoList.toString())
                _todoList.value = todoList
            } catch (e: Exception) {
                Log.e("Ranking", "에러: ${e.message}")
            }
        }
    }

    //db 필요
    val userName: String
        get() = userInfo.value?.nickname ?: "Unknown"
    val totalPomodoro: Int //TODO: 확인 필요
        get() = userInfo.value?.totalPomo?:0
    var introduce = "default introduce text1"
    var friendList = mutableStateListOf<Friend>()
        private set


    fun updateIntroduce(newIntroduce : String){
        introduce = newIntroduce
    }

    fun incrementPomodoroAndEvaluate(context: Context) {
        val newToday = _todayPomodoro.value + 1
        _todayPomodoro.value = newToday

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

                _todayPomodoro.value = 0
                ChallengePrefs.resetTodayPomodoro(context)
            } else {
                loadDailyChallenges(savedDailyStates)
                _todayPomodoro.value = ChallengePrefs.loadTodayPomodoro(context) // 오늘값 불러오기
            }

            if (permanentChallenges.isEmpty()) {
                loadPermanentChallenges(permanentChallengeStates)
            }
        }
    }

//유저 레벨 정보
    private val _userLevel = mutableStateOf(UserLevelState())
    val userLevel: UserLevelState get() = _userLevel.value

    fun gainXp(xpToAdd: Int) {
        var newXp = _userLevel.value.xp + xpToAdd
        var newLevel = _userLevel.value.level
        var newThreshold = _userLevel.value.xpForNextLevel

        while (newXp >= newThreshold) {
            newXp -= newThreshold
            newLevel++
            newThreshold = LevelConfig.xpThresholdFor(newLevel)
        }

        _userLevel.value = UserLevelState(
            level = newLevel,
            xp = newXp,
            xpForNextLevel = newThreshold
        )
    }


    //db 필요
    fun initializeUserLevelFromDb(level: Int, xp: Int) {
        _userLevel.value = UserLevelState.fromDatabase(level, xp)
    }

    fun evaluateDailyChallenges(context: Context, pomodoroCount: Int) {
        ChallengeManager.checkDailyChallengesAfterTimer(
            context = context,
            scope = viewModelScope,
            dailyChallenges = dailyChallenges,
            userLevel = userLevel,
            pomodoroCount = pomodoroCount,
            viewModel = this
        )
    }

    fun evaluatePermanentChallenges(pomodoroTotal: Int, timerStreak: Int, totalCompleted: Int) {
        ChallengeManager.checkPermanentChallenges(
            permanentChallenges = permanentChallenges,
            userLevel = userLevel,
            pomodoroTotal = pomodoroTotal,
            timerStreak = timerStreak,
            totalCompleted = totalCompleted,
            viewModel = this
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


    // 친구 추가 관련 함수
    fun loadFriendsFromDb() {
        viewModelScope.launch {
            // 예시: DB 호출해서 리스트 가져오기 (추후 Retrofit/Supabase 등과 연동)
            val dbFriends = listOf(
                Friend("현수", 5, 10, R.drawable.baseline_person_24),
                Friend("민수", 2, 8, R.drawable.baseline_person_24),
                Friend("지영", 3, 9, R.drawable.baseline_person_24)
            )
            friendList.clear()
            friendList.addAll(dbFriends)
        }
    }

    suspend fun tryAddFriend(nickname: String): String {
        // 이미 있는지 확인
        if (friendList.any { it.nickname == nickname }) {
            return "이미 친구 목록에 있습니다"
        }

        // DB에서 유저 존재 여부 확인 (실제 구현 필요)
        val userExistsInDb = listOf("현수", "민수", "지영", "동건").contains(nickname)

        return if (userExistsInDb) {
            // DB에 친구 등록 API 호출 필요 (현재는 생략)
            friendList.add(Friend(nickname, 0, 0, R.drawable.baseline_person_24))
            "친구 추가가 되었습니다"
        } else {
            "해당 닉네임의 유저가 없습니다"
        }
    }

    fun deleteFriend(nickname: String) {
        viewModelScope.launch {
            // 실제 DB에서 친구 삭제 API 호출 필요
            friendList.removeAll { it.nickname == nickname }
        }
    }
}
