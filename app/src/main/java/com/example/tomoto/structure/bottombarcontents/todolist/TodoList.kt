package com.example.tomoto.structure.bottombarcontents.todolist



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
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
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
import com.example.pomato.UIcomponents.MonthlyCalendarWithStudyTimeComposable
import com.example.pomato.UIcomponents.ScreenHeaderComposable
import com.example.pomato.UIcomponents.SimplifiedMonthlyCalendarComposable
import com.example.pomato.UIcomponents.ToDoInputComposable
import com.example.pomato.UIcomponents.ToDoListItemComposable
import com.example.tomoto.structure.datastructures.TomotoViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoScreenWithCalendarComposable2( // Modifier와 PaddingValues를 받도록 수정
    modifier: Modifier = Modifier,
    tomotoViewModel: TomotoViewModel // ViewModel 주입
) {
    // ViewModel에서 데이터 가져오기
    val allTasks by remember { derivedStateOf { tomotoViewModel.allTasks } }
    val studyTimeData by remember { derivedStateOf { tomotoViewModel.studyTimeData } }

    var newTaskText by remember { mutableStateOf("") }
    var headerToggleState by remember { mutableStateOf(true) }


    val initialDate = LocalDate.of(2025, 5, 10) // 또는 ViewModel에서 관리
    var selectedCalendarDate by remember { mutableStateOf(initialDate) }

    val dueDateFormatter = remember { DateTimeFormatter.ofPattern("yyyy.MM.dd") }
    val headerDateFormatter = remember { DateTimeFormatter.ofPattern("yy.MM.dd EEE", Locale.KOREAN) }
    var showDatePickerDialog by remember { mutableStateOf(false) }

    val tasksForSelectedDate by remember(allTasks, selectedCalendarDate) {
        derivedStateOf {
            val selectedDateStr = selectedCalendarDate.format(dueDateFormatter)
            allTasks.filter { it.dueDate == selectedDateStr }
        }
    }
    val datesWithTasks by remember(allTasks) {
        derivedStateOf {
            allTasks
                .mapNotNull {
                    try { LocalDate.parse(it.dueDate, dueDateFormatter) }
                    catch (e: Exception) { null }
                }
                .toSet()
        }
    }


    Column(
        modifier = modifier
            .fillMaxSize()
    ) {

        ScreenHeaderComposable(
            currentDateString = selectedCalendarDate.format(headerDateFormatter).uppercase(),
            toggleState = headerToggleState,
            onToggleChanged = { headerToggleState = it }
        )

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
                    title = { Text("날짜 선택", modifier = Modifier.padding(start = 24.dp, end = 12.dp, top = 16.dp)) },
                    headline = {
                        val formatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault()) }
                        val dateText = dialogDatePickerState.selectedDateMillis?.let { millis ->
                            Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate().format(formatter)
                        } ?: "날짜를 선택해주세요"
                        Text(dateText, modifier = Modifier.padding(start = 24.dp, end = 12.dp, bottom = 12.dp), style = MaterialTheme.typography.headlineSmall)
                    }
                )
            }
        }

        if (headerToggleState) {
            MonthlyCalendarWithStudyTimeComposable(
                yearMonth = YearMonth.from(selectedCalendarDate),
                studyData = studyTimeData.value, // ViewModel에서 가져온 데이터 사용
                selectedDate = selectedCalendarDate,
                onDateSelected = { date -> selectedCalendarDate = date }
            )
        } else {
            SimplifiedMonthlyCalendarComposable(
                yearMonth = YearMonth.from(selectedCalendarDate),
                selectedDate = selectedCalendarDate,
                datesWithTasks = datesWithTasks, // ViewModel 기반으로 계산된 데이터 사용
                onDateSelected = { date -> selectedCalendarDate = date }
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        ToDoInputComposable(
            currentTaskText = newTaskText,
            onTaskTextChange = { newTaskText = it },
            onAddTask = {
                tomotoViewModel.addTask(newTaskText, selectedCalendarDate) // ViewModel 함수 호출
                newTaskText = ""
            },
            onAddDate = { showDatePickerDialog = true }
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
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(
                    items = tasksForSelectedDate,
                    key = { task -> task.id }
                ) { task ->
                    ToDoListItemComposable(
                        item = task,
                        onToggleComplete = { updatedTask ->
                            tomotoViewModel.updateTask(updatedTask)
                        },
                        onDeleteItem = { taskToDelete ->
                            tomotoViewModel.deleteTask(taskToDelete)
                        }
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
                }
            }
        }
    }
}