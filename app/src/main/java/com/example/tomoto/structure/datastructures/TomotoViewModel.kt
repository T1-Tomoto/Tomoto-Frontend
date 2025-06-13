package com.example.tomoto.structure.datastructures

import UserLevelState
import android.app.Service
import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tomoto.R
import com.example.tomoto.structure.bottombarcontents.rank.Friend
import com.example.tomoto.structure.bottombarcontents.todolist.ToDoItem
import com.example.tomoto.structure.data.dto.request.AddTodoReq
import com.example.tomoto.structure.data.dto.request.LevelUpdateReq
import com.example.tomoto.structure.data.dto.response.UserInfoRes
import com.example.tomoto.structure.data.service.ServicePool
import com.example.tomoto.structure.data.service.UserService
import com.example.tomoto.structure.model.Challenge
import com.example.tomoto.structure.model.ChallengeListFactory
import com.example.tomoto.structure.model.LevelConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class TomotoViewModel : ViewModel() {

    private val _userInfo = MutableStateFlow<UserInfoRes?>(null)
    val userInfo: StateFlow<UserInfoRes?> = _userInfo

    private val _todayPomodoro = MutableStateFlow(0)
    val todayPomodoro: StateFlow<Int> = _todayPomodoro

    val userName: String get() = userInfo.value?.nickname ?: "Unknown"
    val totalPomodoro: Int get() = userInfo.value?.totalPomo ?: 0
    var introduce = "default introduce text1"
    var friendList = mutableStateListOf<Friend>()
        private set
    var musicList = mutableStateListOf<String>()
        private set
    var dailyChallenges = mutableStateListOf<Challenge>()
        private set
    var permanentChallenges = mutableStateListOf<Challenge>()
        private set
    private val permanentChallengeStates = listOf(
        false, false, false, false, false,
        false, false, false, false, false, false, false, false
    )
    private val _userLevel = mutableStateOf(UserLevelState())
    val userLevel: UserLevelState get() = _userLevel.value

    fun updateIntroduce(newIntroduce : String){
        introduce = newIntroduce
    }

    fun incrementPomodoroAndEvaluate(context: Context) {
        val newToday = _todayPomodoro.value + 1
        _todayPomodoro.value = newToday

        viewModelScope.launch {
            ChallengePrefs.saveTodayPomodoro(context, newToday)
        }

        evaluateDailyChallenges(context, pomodoroCount = newToday)
        evaluatePermanentChallenges(
            pomodoroTotal = totalPomodoro,
            timerStreak = 0,
            totalCompleted = dailyChallenges.count { it.isCompleted } + permanentChallenges.count { it.isCompleted }
        )
    }

    fun addMusicUrl(url: String) {
        MusicManager.addMusicUrl(musicList, url)
    }

    fun removeMusicUrl(url: String) {
        MusicManager.removeMusicUrl(musicList, url)
    }

    fun editMusicUrl(oldUrl: String, newUrl: String) {
        MusicManager.editMusicUrl(musicList, oldUrl, newUrl)
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
                _todayPomodoro.value = ChallengePrefs.loadTodayPomodoro(context)
            }

            if (permanentChallenges.isEmpty()) {
                loadPermanentChallenges(permanentChallengeStates)
            }
        }
    }

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

    fun gainXp(xpToAdd: Int) {
        var newXp = _userLevel.value.xp + xpToAdd
        var newLevel = _userLevel.value.level
        var newThreshold = _userLevel.value.xpForNextLevel

        val req = LevelUpdateReq(newLevel, newXp)
        while (newXp >= newThreshold) {
            newXp -= newThreshold
            newLevel++
            newThreshold = LevelConfig.xpThresholdFor(newLevel)
            viewModelScope.launch {
                ServicePool.userService.levelUpdate(req)
            }

        _userLevel.value = UserLevelState(
            level = newLevel,
            xp = newXp,
            xpForNextLevel = newThreshold
        )

        viewModelScope.launch {
            val levelUpdateRequest = LevelUpdateReq(level = newLevel, xp = newXp) // LevelUpdateReq의 실제 필드에 맞게 수정해야 합니다.

            try {
                ServicePool.userService.levelUpdate(levelUpdateRequest)
                Log.d("TomotoViewModel", "Level update request sent: $levelUpdateRequest")
            } catch (e: Exception) {
                Log.e("TomotoViewModel", "Failed to send level update: ${e.message}", e)
            }
        }
    }

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

    fun loadFriendsFromDb() {
        viewModelScope.launch {
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
        if (friendList.any { it.nickname == nickname }) {
            return "이미 친구 목록에 있습니다"
        }
        val userExistsInDb = listOf("현수", "민수", "지영", "동건").contains(nickname)
        return if (userExistsInDb) {
            friendList.add(Friend(nickname, 0, 0, R.drawable.baseline_person_24))
            "친구 추가가 되었습니다"
        } else {
            "해당 닉네임의 유저가 없습니다"
        }
    }

    fun deleteFriend(nickname: String) {
        viewModelScope.launch {
            friendList.removeAll { it.nickname == nickname }
        }
    }
    //todo
    private val _allTasks = mutableStateListOf<ToDoItem>()
    val allTasks: List<ToDoItem> get() = _allTasks

    private val _pomoRecords = mutableStateListOf<Pair<LocalDate, Int>>()
    val pomoCountData: State<Map<LocalDate, Int>> = derivedStateOf {
        _pomoRecords.groupBy { it.first }.mapValues { entry -> entry.value.sumOf { it.second } }
    }
    private val dueDateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

    fun loadDataAfterLogin() {
        Log.d("ViewModel", "로그인 후 데이터 로딩을 시작합니다.")
        fetchUserInfo()
        fetchTodayPomodoro()
        fetchAllTodoList()
        fetchPomoHistory()
    }

    fun fetchUserInfo() {
        viewModelScope.launch {
            try {
                val info = ServicePool.userService.info()
                Log.i("유저 정보", info.toString())
                _userInfo.value = info
                initializeUserLevelFromDb(info.level, info.xp)
            } catch (e: Exception) {
                Log.e("UserInfo", "유저 정보 로딩 실패: ${e.message}")
            }
        }
    }

    fun fetchTodayPomodoro() {
        viewModelScope.launch {
            try {
                val todayPomo = ServicePool.pomoService.getTodayPomo()
                Log.i("TodayPomo", "오늘 뽀모도로: $todayPomo")
                _todayPomodoro.value = todayPomo
            } catch (e: Exception) {
                Log.e("TodayPomo", "오늘 뽀모도로 로딩 실패: ${e.message}")
            }
        }
    }

    fun fetchAllTodoList() {
        viewModelScope.launch {
            try {
                val todoListFromApi = ServicePool.todoService.getAllTodo()
                Log.d("ToDoDateCheck", "서버 응답 데이터: $todoListFromApi")

                val mappedTasks = todoListFromApi.mapNotNull { apiTodo ->
                    ToDoItem(
                        id = apiTodo.todoId.toString(),
                        text = apiTodo.content,
                        isCompleted = apiTodo.completed,
                        dueDate = apiTodo.dueDate?.format(dueDateFormatter) ?: ""
                    )
                }

                _allTasks.clear()
                _allTasks.addAll(mappedTasks)
                Log.d("ViewModelState", "ViewModel의 _allTasks 개수: ${_allTasks.size}")

            } catch (e: Exception) {
                Log.e("ToDoList", "투두리스트 로딩 실패: ${e.message}")
            }
        }
    }

    fun fetchPomoHistory() {
        viewModelScope.launch {
            try {
                val history = ServicePool.pomoService.getAllPomoHistory()
                _pomoRecords.clear()
                history.forEach { record ->
                    _pomoRecords.add(record.createdAt.toLocalDate() to record.pomoNum)
                }
            } catch (e: Exception) {
                Log.e("PomoHistory", "뽀모도로 기록 로딩 실패: ${e.message}")
            }
        }
    }

    fun addTask(text: String, dueDate: LocalDateTime) {
        viewModelScope.launch {
            if (text.isNotBlank()) {
                try {
//                    val dueDateTime = dueDate.atStartOfDay().toInstant(ZoneOffset.UTC)
                    val request = AddTodoReq(content = text, dueDate = dueDate)
                    Log.d("ToDoDateCheck", "서버로 보내는 요청: $request")

                    ServicePool.todoService.addTodo(request)
                    fetchAllTodoList()

                } catch (e: Exception) {
                    Log.e("ToDoList", "할 일 추가 실패: ${e.message}")
                }
            }
        }
    }

    fun deleteTask(taskToDelete: ToDoItem) {
        viewModelScope.launch {
            try {
                val todoId = taskToDelete.id.toLong()
                ServicePool.todoService.deleteTodo(todoId)
                _allTasks.remove(taskToDelete)
            } catch (e: Exception) {
                Log.e("ToDoList", "할 일 삭제 실패: ${e.message}")
            }
        }
    }

    fun updateTask(updatedTask: ToDoItem) {
        viewModelScope.launch {
            try {
                ServicePool.todoService.toggleTodo(updatedTask.id.toLong())

                val index = _allTasks.indexOfFirst { it.id == updatedTask.id }
                if (index != -1) {
                    _allTasks[index] = updatedTask
                }
                Log.d("ToDoList", "ID ${updatedTask.id}의 완료 상태가 업데이트되었습니다.")

            } catch (e: Exception) {
                Log.e("ToDoList", "할 일 상태 업데이트 실패: ${e.message}")
            }
        }
    }
}