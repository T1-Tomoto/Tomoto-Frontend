package com.example.tomoto.structure.datastructures

import UserLevelState
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
import com.example.tomoto.structure.data.dto.request.AddFriendReq
import com.example.tomoto.structure.data.dto.request.AddTodoReq
import com.example.tomoto.structure.data.dto.request.LevelUpdateReq
import com.example.tomoto.structure.data.dto.request.UpdateMusicReq
import com.example.tomoto.structure.data.dto.response.MusicListRes
import com.example.tomoto.structure.data.dto.response.UserInfoRes
import com.example.tomoto.structure.data.service.ServicePool
import com.example.tomoto.structure.model.Challenge
import com.example.tomoto.structure.model.ChallengeListFactory
import com.example.tomoto.structure.model.LevelConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TomotoViewModel : ViewModel() {

    private val _userInfo = MutableStateFlow<UserInfoRes?>(null)
    val userInfo: StateFlow<UserInfoRes?> = _userInfo

    private val _todayPomodoro = MutableStateFlow(0)
    val todayPomodoro: StateFlow<Int> = _todayPomodoro

    val userName: String get() = userInfo.value?.nickname ?: "Unknown"
    val totalPomodoro: Int get() = userInfo.value?.totalPomo ?: 0
    val introduce: String get() = userInfo.value?.bio ?: "default introduce text1"

    var friendList = mutableStateListOf<Friend>()
        private set
    var dailyChallenges = mutableStateListOf<Challenge>()
        private set
    var permanentChallenges = mutableStateListOf<Challenge>()
        private set
    private val _userLevel = mutableStateOf(UserLevelState())
    val userLevel: UserLevelState get() = _userLevel.value

    private val _musicList = MutableStateFlow<List<String>>(emptyList())
    val musicList: StateFlow<List<String>> = _musicList

    fun logIntroduceInfo() {
        Log.i("introduce", "userInfo.value?.bio: ${userInfo.value?.bio}")
        Log.i("introduce", "introduce 변수: $introduce")
    }

    fun updateIntroduce(newIntroduce: String) {
        viewModelScope.launch {
            try {
                ServicePool.userService.updateUserBio(newIntroduce)
                _userInfo.value = _userInfo.value?.copy(bio = newIntroduce)

                Log.d("TomotoViewModel", "자기소개 업데이트 성공: $newIntroduce")
            } catch (e: Exception) {
                Log.e("TomotoViewModel", "자기소개 업데이트 실패: ${e.message}")
            }
        }
    }
    fun incrementPomodoroAndEvaluate(context: Context, timerStreak: Int) {
        val newToday = _todayPomodoro.value + 1
        Log.d("PomodoroCheck", "이전 값: ${_todayPomodoro.value}, 새 값: $newToday")
        _todayPomodoro.value = newToday
        Log.d("PomodoroCheck", "변경 완료: ${_todayPomodoro.value}")


        viewModelScope.launch {
            ChallengePrefs.saveTodayPomodoro(context, newToday)
        }

        evaluateDailyChallenges(context, timerStreak, pomodoroCount = newToday)
        evaluatePermanentChallenges(
            pomodoroTotal = totalPomodoro,
            timerStreak = timerStreak,
            totalCompleted = dailyChallenges.count { it.isCompleted } + permanentChallenges.count { it.isCompleted }
        )

        viewModelScope.launch {
            try {
                val response = ServicePool.pomoService.addPomodoro()
                Log.d("PomodoroAPI", "POST /pomos/add 성공, 응답: $response")
                fetchPomoHistory()
            } catch (e: Exception) {
                Log.e("PomodoroAPI", "POST /pomos/add 실패: ${e.message}")
            }
        }
    }

    fun fetchMusicList() {
        viewModelScope.launch {
            try {
                val fetchedList: List<MusicListRes> = ServicePool.musicService.getMusicList()
                _musicList.value = fetchedList.map { it.url }
                Log.d("MusicList", "음악 목록 불러오기 성공: $musicList")
            } catch (e: Exception) {
                Log.e("MusicList", "음악 목록 불러오기 실패: ${e.message}")
            }
        }
    }


    fun addMusicUrl(url: String) {
        viewModelScope.launch {
            try {
                ServicePool.musicService.addMusic(url)
                fetchMusicList() // 추가 후 목록 갱신
                Log.d("MusicList", "음악 추가 성공: $url")
            } catch (e: Exception) {
                Log.e("MusicList", "음악 추가 실패: ${e.message}")
            }
        }
    }

    fun removeMusicUrl(url: String) {
        viewModelScope.launch {
            try {
                ServicePool.musicService.deleteMusic(url)
                fetchMusicList() // 삭제 후 목록 갱신
                Log.d("MusicList", "음악 삭제 성공: $url")
            } catch (e: Exception) {
                Log.e("MusicList", "음악 삭제 실패: ${e.message}")
            }
        }
    }
    fun editMusicUrl(oldUrl: String, newUrl: String) {
        viewModelScope.launch {
            try {
                val request = UpdateMusicReq(oldUrl, newUrl)
                ServicePool.musicService.updateMusic(request) // 함수명 오타: updatdMusic -> updateMusic으로 수정 권장
                fetchMusicList() // 수정 후 목록 갱신
                Log.d("MusicList", "음악 수정 성공: $oldUrl -> $newUrl")
            } catch (e: Exception) {
                Log.e("MusicList", "음악 수정 실패: ${e.message}")
            }
        }
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

        viewModelScope.launch {
            val req = LevelUpdateReq(newLevel, newXp)
            ServicePool.userService.levelUpdate(req)
        }
    }

    fun initializeUserLevelFromDb(level: Int, xp: Int) {
        _userLevel.value = UserLevelState.fromDatabase(level, xp)
    }

    fun evaluateDailyChallenges(context: Context, timerStreak: Int,pomodoroCount: Int) {
        ChallengeManager.checkDailyChallengesAfterTimer(
            context = context,
            scope = viewModelScope,
            dailyChallenges = dailyChallenges,
            userLevel = userLevel,
            pomodoroCount = pomodoroCount,
            viewModel = this,
            timerStreak = timerStreak
        )
    }

    fun evaluatePermanentChallenges(pomodoroTotal: Int, timerStreak: Int, totalCompleted: Int) {
        ChallengeManager.checkPermanentChallenges(
            permanentChallenges = permanentChallenges,
            pomodoroTotal = pomodoroTotal,
            timerStreak = timerStreak,
            totalCompleted = totalCompleted,
            viewModel = this,
            scope = viewModelScope
        )
    }

    // 친구 랭킹 관련 ------------------------------------

    val friendsRanking = MutableStateFlow<List<Friend>>(emptyList())

    fun fetchFriendsRanking() {
        viewModelScope.launch {
            try {
                val res = ServicePool.rankService.getFriendsRanking()
                friendsRanking.value = res.map {
                    Friend(it.nickname, it.pomoNum, 0, R.drawable.baseline_person_24)
                }.sortedByDescending { it.pomodoroCount }
            } catch (e: Exception) {
                Log.e("FriendRanking", "친구 랭킹 불러오기 실패: ${e.message}")
            }
        }
    }

    fun fetchAddFriend(req: AddFriendReq) {
        viewModelScope.launch {
            try {
                ServicePool.rankService.addFriend(req)
                fetchFriendsRanking()  // 친구 추가 후 목록 갱신
            } catch (e: Exception) {
                Log.e("FriendAdd", "친구 추가 실패: ${e.message}")
            }
        }
    }

    fun fetchDeleteFriend(nickname: String) {
        viewModelScope.launch {
            try {
                ServicePool.rankService.deleteFriend(nickname)
                fetchFriendsRanking()  // 삭제 후 목록 갱신
            } catch (e: Exception) {
                Log.e("FriendDelete", "친구 삭제 실패: ${e.message}")
            }
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
                Log.i("introduce", "bio 값: ${info.bio}")
                initializeUserLevelFromDb(info.level, info.xp)
                loadPermanentChallenges(info.challenges)
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
                Log.i("pomohistory", "pomo기록 전체: ${history}")
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
