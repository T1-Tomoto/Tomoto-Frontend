package com.example.pomato.UIcomponents

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import java.time.LocalDate
import java.time.YearMonth

// 2. 월간 달력 (공부 시간 포함) Composable
@Composable
fun MonthlyCalendarWithStudyTimeComposable(
    yearMonth: YearMonth,
    pomoData: Map<LocalDate, Int>, //뽀모도로로
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstOfMonth = yearMonth.atDay(1)
    // 월요일(1) ~ 일요일(7)을 0(월) ~ 6(일)로 변환
    val firstDayOfWeek = (firstOfMonth.dayOfWeek.value + 6) % 7

    val days = mutableListOf<DayStudyData?>()
    // 이전 달의 날짜 채우기 (월요일 시작 기준)
    val prevMonth = yearMonth.minusMonths(1)
    val daysInPrevMonth = prevMonth.lengthOfMonth()
    for (i in 0 until firstDayOfWeek) {
        val day = daysInPrevMonth - firstDayOfWeek + i + 1
        days.add(DayStudyData(day, prevMonth.atDay(day), null, false))
    }

    // 현재 달의 날짜 채우기
    for (day in 1..daysInMonth) {
        val date = yearMonth.atDay(day)
        days.add(DayStudyData(day, date, pomoData[date], true, date == selectedDate))
    }

    // 다음 달의 날짜 채우기 (총 6주 = 42칸 기준)
    val nextMonth = yearMonth.plusMonths(1)
    var dayCounter = 1
    while (days.size < 42) { // 6주 * 7일
        days.add(DayStudyData(dayCounter, nextMonth.atDay(dayCounter), null, false))
        dayCounter++
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        // 요일 헤더 (월 화 수 목 금 토 일)
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
            modifier = Modifier.fillMaxWidth(), // Height를 지정하거나 content에 맞게 조절
            userScrollEnabled = false
        ) {
            items(days.filterNotNull().take(42)) { dayData -> // 최대 42개 아이템만 그리도록
                DayCellComposable(dayData = dayData, onDateSelected = onDateSelected)
            }
        }
    }
}


@Composable
fun DayCellComposable(
    dayData: DayStudyData,
    onDateSelected: (LocalDate) -> Unit
) {
    // 1. 뽀모도로 횟수를 가져옵니다.
    val pomoCount = dayData.pomoCount ?: 0

    // 2. 색상 농도 계산 로직을 '횟수' 기준으로 변경
    // 하루 최대 10회를 기준으로 색상 농도를 계산 (이 값은 조절 가능)
    val maxPomoCountReference = 10
    val pomoRatio = (pomoCount.toFloat() / maxPomoCountReference).coerceIn(0f, 1f)

    val baseCellColor = Color(0xFFF48FB1) // 기본 테마 색상

    val cellColor = when {
        dayData.isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
        dayData.isCurrentMonth && pomoCount > 0 -> {
            val minAlpha = 0.25f
            val maxAlpha = 0.9f
            val calculatedAlpha = minAlpha + (pomoRatio * (maxAlpha - minAlpha))
            baseCellColor.copy(alpha = calculatedAlpha.coerceIn(minAlpha, maxAlpha))
        }
        else -> Color.Transparent
    }

    val textColor = if (dayData.isCurrentMonth) {
        if (pomoRatio > 0.6f && cellColor.alpha > 0.5f) {
            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
        } else {
            MaterialTheme.colorScheme.onSurface
        }
    } else {
        Color.LightGray.copy(alpha = 0.7f)
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(cellColor)
            .clickable(enabled = dayData.isCurrentMonth) { onDateSelected(dayData.date) }
            .border(
                width = if (dayData.isSelected) 1.5.dp else 0.dp,
                color = if (dayData.isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = dayData.dayOfMonth.toString(),
                fontSize = 13.sp,
                color = textColor,
                fontWeight = if (dayData.isSelected || dayData.isCurrentMonth) FontWeight.Medium else FontWeight.Light
            )
            // 3. 뽀모도로 횟수를 텍스트로 표시
            if (dayData.isCurrentMonth && dayData.pomoCount != null && pomoCount > 0) {
                Text(
                    text = "${dayData.pomoCount}회", // "5회" 와 같은 형식으로 표시
                    fontSize = 10.sp,
                    color = textColor.copy(alpha = 0.8f)
                )
            } else if (dayData.isCurrentMonth) {
                Spacer(modifier = Modifier.height(15.dp)) // 높이 조절
            }
        }
    }
}
