package com.example.pomato.UIcomponents


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoScreenWithCalendarComposable2() {
    var allTasks by remember { mutableStateOf(listOf<ToDoItem>()) }
    var newTaskText by remember { mutableStateOf("") }
    var headerToggleState by remember { mutableStateOf(true) }

    val initialDate = LocalDate.of(2025, 5, 10)
    var selectedCalendarDate by remember { mutableStateOf(initialDate) }

    val dueDateFormatter = remember { DateTimeFormatter.ofPattern("yyyy.MM.dd") }
    val headerDateFormatter = remember { DateTimeFormatter.ofPattern("yy.MM.dd EEE", Locale.KOREAN) }

    var showDatePickerDialog by remember { mutableStateOf(false) }

    // 임시 뽀모도로 횟수 데이터 (Int 타입)
    val pomoCountData = remember {
        mapOf(
            LocalDate.of(2025, 5, 1) to 8, LocalDate.of(2025, 5, 2) to 9,
            LocalDate.of(2025, 5, 3) to 6, LocalDate.of(2025, 5, 4) to 4,
            LocalDate.of(2025, 5, 5) to 7, LocalDate.of(2025, 5, 6) to 3,
            LocalDate.of(2025, 5, 8) to 7, LocalDate.of(2025, 5, 9) to 10,
            LocalDate.of(2025, 5, 10) to 9, LocalDate.of(2025, 5, 11) to 8,
            LocalDate.of(2025, 5, 12) to 10, LocalDate.of(2025, 5, 13) to 9,
            LocalDate.of(2025, 5, 14) to 5, LocalDate.of(2025, 5, 15) to 10,
            LocalDate.of(2025, 5, 16) to 4, LocalDate.of(2025, 5, 20) to 11,
            LocalDate.of(2025, 5, 22) to 5, LocalDate.of(2025, 5, 25) to 8,
            LocalDate.of(2025, 5, 28) to 4, LocalDate.of(2025, 5, 31) to 5
        )
    }

    val datesWithTasks by remember(allTasks) {
        derivedStateOf {
            allTasks
                .mapNotNull {
                    try {
                        LocalDate.parse(it.dueDate, dueDateFormatter)
                    } catch (e: Exception) {
                        null
                    }
                }
                .toSet()
        }
    }

    LaunchedEffect(Unit) {
        allTasks = listOf(
            ToDoItem(text = "5월 10일 디자인 검토", dueDate = "2025.05.10"),
            ToDoItem(text = "5월 10일 회의 준비", dueDate = "2025.05.10", isCompleted = true),
            ToDoItem(text = "5월 12일 보고서 작성", dueDate = "2025.05.12"),
            ToDoItem(text = "헬스장 가기", dueDate = selectedCalendarDate.format(dueDateFormatter))
        )
    }

    val tasksForSelectedDate by remember (allTasks, selectedCalendarDate) {
        derivedStateOf {
            val selectedDateStr = selectedCalendarDate.format(dueDateFormatter)
            allTasks.filter { it.dueDate == selectedDateStr }
        }
    }

    Scaffold(
        topBar = {
            ScreenHeaderComposable(
                currentDateString = selectedCalendarDate.format(headerDateFormatter).uppercase(),
                toggleState = headerToggleState,
                onToggleChanged = { headerToggleState = it }
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (showDatePickerDialog) {
                val dialogDatePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = selectedCalendarDate
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
                )

                DatePickerDialog (
                    onDismissRequest = { showDatePickerDialog = false },
                    confirmButton = {
                        Button(onClick = {
                            showDatePickerDialog = false
                            dialogDatePickerState.selectedDateMillis?.let { millis ->
                                selectedCalendarDate = Instant.ofEpochMilli(millis)
                                    .atZone(ZoneId.systemDefault()).toLocalDate()
                            }
                        }) { Text("확인") }
                    },
                    dismissButton = {
                        Button(onClick = { showDatePickerDialog = false }) { Text("취소") }
                    }
                ) {
                    DatePicker(
                        state = dialogDatePickerState,
                        title = {
                            Text(
                                text = "날짜 선택",
                                modifier = Modifier.padding(start = 24.dp, end = 12.dp, top = 16.dp)
                            )
                        },
                        headline = {
                            val formatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault()) }
                            val dateText = dialogDatePickerState.selectedDateMillis?.let { millis ->
                                Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate().format(formatter)
                            } ?: "날짜를 선택해주세요"

                            Text(
                                text = dateText,
                                modifier = Modifier.padding(start = 24.dp, end = 12.dp, bottom = 12.dp),
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    )
                }
            }

            if (headerToggleState) {
                MonthlyCalendarWithStudyTimeComposable(
                    yearMonth = YearMonth.from(selectedCalendarDate),
                    pomoData = pomoCountData,
                    selectedDate = selectedCalendarDate,
                    onDateSelected = { date -> selectedCalendarDate = date }
                )
            } else {
                SimplifiedMonthlyCalendarComposable(
                    yearMonth = YearMonth.from(selectedCalendarDate),
                    selectedDate = selectedCalendarDate,
                    datesWithTasks = datesWithTasks,
                    onDateSelected = { date -> selectedCalendarDate = date }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            ToDoInputComposable(
                currentTaskText = newTaskText,
                onTaskTextChange = { newTaskText = it },
                onAddTask = {
                    if (newTaskText.isNotBlank()) {
                        val newToDo = ToDoItem(
                            text = newTaskText,
                            dueDate = selectedCalendarDate.format(dueDateFormatter)
                        )
                        allTasks = allTasks + newToDo
                        newTaskText = ""
                    }
                },
                onAddDate = {
                    showDatePickerDialog = true
                }
            )

            if (tasksForSelectedDate.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "${selectedCalendarDate.format(DateTimeFormatter.ofPattern("M월 d일"))}에는 할 일이 없어요! 🎉",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(
                        items = tasksForSelectedDate,
                        key = { task -> task.id }
                    ) { task ->
                        ToDoListItemComposable(
                            item = task,
                            onToggleComplete = { updatedTask ->
                                allTasks = allTasks.map {
                                    if (it.id == updatedTask.id) updatedTask else it
                                }
                            },
                            onDeleteItem = { taskToDelete ->
                                allTasks = allTasks.filterNot { it.id == taskToDelete.id }
                            }
                        )
                        Divider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}