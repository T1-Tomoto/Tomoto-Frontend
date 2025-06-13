package com.example.tomoto.structure.bottombarcontents.todolist

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun SimplifiedMonthlyCalendarComposable(
    yearMonth: YearMonth,
    selectedDate: LocalDate,
    datesWithTasks: Set<LocalDate>,
    onDateSelected: (LocalDate) -> Unit
) {
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstOfMonth = yearMonth.atDay(1)
    val firstDayOfWeek = (firstOfMonth.dayOfWeek.value + 6) % 7

    val days = mutableListOf<LocalDate?>()
    for (i in 0 until firstDayOfWeek) {
        days.add(null)
    }
    for (day in 1..daysInMonth) {
        days.add(yearMonth.atDay(day))
    }
    while (days.size % 7 != 0 && days.size < 42) { // 최대 6주까지만 표시
        days.add(null)
    }
    // 만약 6주(42칸)를 정확히 채우고 싶다면:
    // while (days.size < 42) { days.add(null) }


    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            val weekDays = listOf("월", "화", "수", "목", "금", "토", "일")
            weekDays.forEach { dayLabel ->
                Text(
                    text = dayLabel,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth(),
            userScrollEnabled = false
        ) {
            items(days.take(42)) { date -> // 최대 42개 아이템만
                if (date != null) {
                    SimplifiedDayCellComposable(
                        date = date,
                        isSelected = date == selectedDate,
                        hasTask = datesWithTasks.contains(date),
                        onClick = { onDateSelected(date) }
                    )
                } else {
                    Spacer(Modifier.aspectRatio(1f).padding(2.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

// 5. 간략 달력용 날짜 셀 Composable
@Composable
fun SimplifiedDayCellComposable(
    date: LocalDate,
    isSelected: Boolean,
    hasTask: Boolean,
    onClick: () -> Unit
) {
    val isSunday = date.dayOfWeek == DayOfWeek.SUNDAY
    val isSaturday = date.dayOfWeek == DayOfWeek.SATURDAY

    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        isSunday -> Color.Red.copy(alpha = 0.8f)
        isSaturday -> MaterialTheme.colorScheme.primary.copy(alpha = 0.7f) // 예시: 토요일 파란색 계열
        else -> MaterialTheme.colorScheme.onSurface
    }

    val cellShape = RoundedCornerShape(8.dp)

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(3.dp)
            .clip(cellShape)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = date.dayOfMonth.toString(),
                fontSize = 14.sp,
                color = textColor,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            if (hasTask) {
                Canvas (modifier = Modifier.size(4.dp).padding(top = 2.dp)) {
                    drawCircle(
                        color = Color.Gray.copy(alpha = 0.7f),
                        radius = size.minDimension / 2
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}