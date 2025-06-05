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
    studyData: Map<LocalDate, String>, // 날짜별 공부 시간 데이터
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
        days.add(DayStudyData(day, date, studyData[date], true, date == selectedDate))
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
    val studyMinutes = parseStudyTimeToMinutes(dayData.studyTime)

    // 색상 농도 계산 로직
    // 최대 공부 시간 기준 (예: 8시간 = 480분). 이 값을 기준으로 비율을 계산합니다.
    // 이 값은 앱의 특성에 맞게 조절할 수 있습니다.
    val maxStudyMinutesReference = 8 * 60
    val studyRatio = (studyMinutes.toFloat() / maxStudyMinutesReference).coerceIn(0f, 1f)

    val baseCellColor = Color(0xFFF48FB1) // 기본 테마 색상 사용

    val cellColor = when {
        dayData.isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f) // 선택된 날짜는 다른 색으로 강조
        dayData.isCurrentMonth && studyMinutes > 0 -> {
            // 공부 시간에 비례하여 투명도 조절
            // 최소 투명도: 아주 적은 시간 공부해도 보이도록 (예: 0.15f)
            // 최대 투명도: 가장 많이 공부했을 때 (예: 0.9f 또는 1.0f)
            val minAlpha = 0.25f
            val maxAlpha = 0.9f // 좀 더 진하게 하려면 1.0f 까지 가능

            // studyRatio가 0일 때 minAlpha, 1일 때 maxAlpha가 되도록 계산
            val calculatedAlpha = minAlpha + (studyRatio * (maxAlpha - minAlpha))

            baseCellColor.copy(alpha = calculatedAlpha.coerceIn(minAlpha, maxAlpha))
        }
        dayData.isCurrentMonth -> Color.Transparent // 공부 시간이 없는 현재 달의 날짜
        else -> Color.Transparent // 이전/다음 달 날짜
    }

    val textColor = if (dayData.isCurrentMonth) {
        // 배경색이 진해질 경우 텍스트 색상이 잘 보이도록 조정 가능 (예: 흰색)
        if (studyRatio > 0.6f && cellColor.alpha > 0.5f) { // 예시: 공부 많이 해서 배경이 진하면
            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f) // primary 색상 위의 텍스트 색
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
            .background(cellColor) // 계산된 cellColor 사용
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
                color = textColor, // 계산된 textColor 사용
                fontWeight = if (dayData.isSelected || dayData.isCurrentMonth) FontWeight.Medium else FontWeight.Light
            )
            // 공부 시간을 직접 표시하는 부분은 유지 (또는 제거/변경 가능)
            if (dayData.isCurrentMonth && dayData.studyTime != null && studyMinutes > 0) {
                Text(
                    text = dayData.studyTime, // HH:mm 형식
                    fontSize = 9.sp,
                    color = textColor.copy(alpha = 0.8f) // 텍스트 색상에 맞춰 투명도 조절
                )
            } else if (dayData.isCurrentMonth) {
                // 공부 시간이 없거나 0분일 때 공간 확보
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}