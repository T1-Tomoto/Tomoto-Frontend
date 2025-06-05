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
    var selectedBottomTabIndex by remember { mutableStateOf(1) }

    val initialDate = LocalDate.of(2025, 5, 10)
    var selectedCalendarDate by remember { mutableStateOf(initialDate) }

    val dueDateFormatter = remember { DateTimeFormatter.ofPattern("yyyy.MM.dd") }
    val headerDateFormatter = remember { DateTimeFormatter.ofPattern("yy.MM.dd EEE", Locale.KOREAN) }

    var showDatePickerDialog by remember { mutableStateOf(false) }

    // 임시 공부 시간 데이터
    val studyTimeData = remember {
        mapOf(
            LocalDate.of(2025, 5, 1) to "06:44", LocalDate.of(2025, 5, 2) to "07:11",
            LocalDate.of(2025, 5, 3) to "04:59", LocalDate.of(2025, 5, 4) to "03:08",
            LocalDate.of(2025, 5, 5) to "05:11", LocalDate.of(2025, 5, 6) to "02:10",
            LocalDate.of(2025, 5, 7) to "03:00", LocalDate.of(2025, 5, 8) to "05:46",
            LocalDate.of(2025, 5, 9) to "08:39", LocalDate.of(2025, 5, 10) to "07:25",
            LocalDate.of(2025, 5, 11) to "06:16", LocalDate.of(2025, 5, 12) to "08:15",
            LocalDate.of(2025, 5, 13) to "07:58", LocalDate.of(2025, 5, 14) to "04:38",
            LocalDate.of(2025, 5, 15) to "08:00", LocalDate.of(2025, 5, 16) to "02:57",
            LocalDate.of(2025, 5, 17) to "00:43", LocalDate.of(2025, 5, 19) to "07:58",
            LocalDate.of(2025, 5, 20) to "09:09", LocalDate.of(2025, 5, 21) to "00:00",
            LocalDate.of(2025, 5, 22) to "03:40", LocalDate.of(2025, 5, 24) to "00:52",
            LocalDate.of(2025, 5, 25) to "06:48", LocalDate.of(2025, 5, 26) to "01:04",
            LocalDate.of(2025, 5, 27) to "04:02", LocalDate.of(2025, 5, 28) to "03:31",
            LocalDate.of(2025, 5, 31) to "04:32"
        )//날짜별뽀모, 오늘까지 todo, 추가,삭제,
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
        bottomBar = {
            //AppBottomNavigation(
              //  selectedTab = selectedBottomTabIndex,
                //onTabSelected = { selectedBottomTabIndex = it }
            //)
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (showDatePickerDialog) {
                // DatePickerDialog가 표시될 때 DatePickerState를 생성하고 초기화합니다.
                val dialogDatePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = selectedCalendarDate
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
                )

                DatePickerDialog (
                    onDismissRequest = { showDatePickerDialog = false },
                    confirmButton = {
                        Button(
                            onClick = {
                                showDatePickerDialog = false
                                // dialogDatePickerState에서 선택된 날짜를 읽어옵니다.
                                dialogDatePickerState.selectedDateMillis?.let { millis ->
                                    selectedCalendarDate = Instant.ofEpochMilli(millis)
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                                }
                            }
                        ) {
                            Text("확인")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showDatePickerDialog = false }
                        ) {
                            Text("취소")
                        }
                    }
                ) {
                    DatePicker(
                        state = dialogDatePickerState, // 새로 생성된 state 사용
                        title = {
                            Text(
                                text = "날짜 선택",
                                modifier = Modifier.padding(start = 24.dp, end = 12.dp, top = 16.dp)
                            )
                        },
                        headline = {
                            val formatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault()) }
                            // dialogDatePickerState에서 날짜를 읽어와 헤드라인에 표시
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
                    studyData = studyTimeData,
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
                    // setSelectionInMillis 호출 대신, 단순히 다이얼로그를 보여주는 상태만 변경합니다.
                    // DatePickerState의 초기화는 위 if (showDatePickerDialog) 블록에서 처리됩니다.
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