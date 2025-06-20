package com.example.tomoto.structure.bottombarcontents.timer

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.tomoto.structure.datastructures.TomotoViewModel

@Composable
fun TimerSettingScreen(
    viewModel: TomotoViewModel,
    onStartFocus: (String, Int, Boolean) -> Unit
) {
    var task by remember { mutableStateOf("") }
    var pomoCount by remember { mutableStateOf(5) }

    // 토마토 색상 팔레트
    val tomatoRed = Color(0xFFE74C3C)
    val tomatoOrange = Color(0xFFFF6B47)
    val creamWhite = Color(0xFFFFF8E7)
    val warmBrown = Color(0xFF8B4513)
    val lightTomato = Color(0xFFFFDDD8)

    val todayPomodoro by viewModel.todayPomodoro.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchTodayPomodoro()
    }
    Log.i("오늘의 뽀모도로", todayPomodoro.toString())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(creamWhite)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .drawBehind {
                    val strokeWidth = 2.dp.toPx()
                    val y = size.height - strokeWidth / 2
                    drawLine(
                        color = tomatoOrange,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = strokeWidth
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            if (task.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "집중하고 싶은 일을 적어주세요!",
                        color = warmBrown,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                }
            }

            BasicTextField(
                value = task,
                onValueChange = { task = it },
                textStyle = TextStyle(
                    color = tomatoRed,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        innerTextField()
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            "몇 뽀모도로 집중할 건가요?",
            color = warmBrown,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { if (pomoCount > 1) pomoCount-- },
                modifier = Modifier.size(80.dp)
            ) {
                Text(
                    "-",
                    fontSize = 60.sp,
                    color = tomatoOrange
                )
            }

            Text(
                text = pomoCount.toString(),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = tomatoRed,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            IconButton(
                onClick = { if (pomoCount < 10) pomoCount++ },
                modifier = Modifier.size(80.dp)
            ) {
                Text(
                    "+",
                    fontSize = 32.sp,
                    color = tomatoOrange
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    onStartFocus(task, pomoCount, true)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE8E8E8),
                    contentColor = warmBrown
                )
            ) {
                Text(
                    text = "예시",
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = {
                    if (task.isNotBlank()) {
                        onStartFocus(task, pomoCount, false)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = tomatoRed,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "집중 시작",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = buildAnnotatedString {
                append("오늘 총 ")
                withStyle(style = SpanStyle(color = tomatoRed, fontWeight = FontWeight.Bold)) {
                    append(todayPomodoro.toString())
                }
                append(" 뽀모도로로 집중하셨네요!")
            },
            fontSize = 16.sp,
            color = warmBrown,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
