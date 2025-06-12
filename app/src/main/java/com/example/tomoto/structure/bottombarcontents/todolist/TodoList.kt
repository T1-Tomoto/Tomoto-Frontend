package com.example.tomoto.structure.bottombarcontents.todolist

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import com.example.tomoto.structure.datastructures.TomotoViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoScreenWithCalendarComposable2(
    tomotoViewModel: TomotoViewModel
) {
    Log.d("UIState", "UI의 allTasks 개수: ${tomotoViewModel.allTasks.size}")
    val allTasks by remember { derivedStateOf { tomotoViewModel.allTasks } }
    val pomoCountData by tomotoViewModel.pomoCountData

    var headerToggleState by remember { mutableStateOf(true) }
    var selectedCalendarDate by remember { mutableStateOf(LocalDateTime.now()) }

    val dueDateFormatter = remember { DateTimeFormatter.ofPattern("yyyy.MM.dd") }
    val headerDateFormatter = remember { DateTimeFormatter.ofPattern("yy.MM.dd EEE", Locale.KOREAN) }
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var showAddTaskDialog by remember { mutableStateOf(false) }

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


    if (showDatePickerDialog) {
        val dialogDatePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedCalendarDate
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                Button(onClick = {
                    showDatePickerDialog = false
                    dialogDatePickerState.selectedDateMillis?.let { millis ->
                        selectedCalendarDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault()).toLocalDateTime()
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

    if (showAddTaskDialog) {
        AddTaskDialog(
            onDismissRequest = { showAddTaskDialog = false },
            onConfirm = { taskText ->
                tomotoViewModel.addTask(taskText, selectedCalendarDate)
                showAddTaskDialog = false
            }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenHeaderComposable(
                currentDateString = selectedCalendarDate.format(headerDateFormatter).uppercase(),
                toggleState = headerToggleState,
                onToggleChanged = { headerToggleState = it }
            )

            if (headerToggleState) {
                MonthlyCalendarWithStudyTimeComposable(
                    yearMonth = YearMonth.from(selectedCalendarDate),
                    pomoData = pomoCountData,
                    selectedDate = selectedCalendarDate.toLocalDate(),
                    onDateSelected = { date -> selectedCalendarDate = date.atStartOfDay() }
                )
            } else {
                SimplifiedMonthlyCalendarComposable(
                    yearMonth = YearMonth.from(selectedCalendarDate),
                    selectedDate = selectedCalendarDate.toLocalDate(),
                    datesWithTasks = datesWithTasks,
                    onDateSelected = { date -> selectedCalendarDate = date.atStartOfDay() }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

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

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.End
        ) {
            FloatingActionButton(
                onClick = { showDatePickerDialog = true },
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Icon(Icons.Default.DateRange, contentDescription = "날짜 선택")
            }
            FloatingActionButton(onClick = { showAddTaskDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "할 일 추가")
            }
        }
    }
}