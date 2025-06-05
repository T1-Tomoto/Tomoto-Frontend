package com.example.tomoto.structure.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.example.pomato.UIcomponents.ToDoItem

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import java.util.concurrent.TimeUnit

// 개별 공부 세션을 나타내는 데이터 클래스
data class StudySession(
    val id: String = UUID.randomUUID().toString(),
    val date: LocalDate,
    val durationInSeconds: Long // 공부 시간 (초 단위)
)

class TomotoViewModel : ViewModel() {



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
        // 실제 앱에서는 DB 등에서 로드해야 합니다.
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