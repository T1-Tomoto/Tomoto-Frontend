package com.example.tomoto.structure.bottombarcontents.timer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.example.tomoto.R

@Composable
fun TimerPlayScreen(
    taskName: String = "",
    pomoCount: Int = 0,
    onCancel: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(true) }
    var showVolume by remember { mutableStateOf(true) }

    val focusTime = 1 * 60  // 집중 시간 1분 (테스트용)
    val restTime = 1 * 60   // 쉬는 시간 1분 (테스트용)

    var currentPhase by remember { mutableStateOf("FOCUS") }  // FOCUS 또는 REST
    var currentPomoIndex by remember { mutableStateOf(0) }    // 현재 몇 번째 반복인지
    var timer by remember { mutableStateOf(focusTime) }

    LaunchedEffect(isPlaying, currentPhase, currentPomoIndex) {
        while (isPlaying && currentPomoIndex < pomoCount) {
            delay(1000)
            timer--

            if (timer <= 0) {
                if (currentPhase == "FOCUS") {
                    // 집중 시간 끝.. 쉬는 시간 시작
                    currentPhase = "REST"
                    timer = restTime
                } else {
                    // 쉬는 시간 끝.. 다음 뽀모도로
                    currentPomoIndex++
                    if (currentPomoIndex < pomoCount) {
                        currentPhase = "FOCUS"
                        timer = focusTime
                    } else {
                        isPlaying = false  // 전체 끝
                        // API 호출 가능: 뽀모도로 카운트 증가
                    }
                }
            }
        }
    }

    val minutes = timer / 60
    val seconds = timer % 60

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            IconButton(onClick = { showVolume = !showVolume }) {
                Icon(
                    painter = painterResource(
                        if (showVolume) R.drawable.baseline_volume_up_24 else R.drawable.baseline_volume_off_24
                    ),
                    contentDescription = "Toggle Sound"
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "$taskName", fontSize = 20.sp, textAlign = TextAlign.Center)
            Text(text = "Round ${currentPomoIndex + 1} of $pomoCount", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = String.format("%02d:%02d", minutes, seconds),
                fontSize = 36.sp
            )
            Text(
                text = if (currentPhase == "FOCUS") "집중 시간" else "쉬는 시간",
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { isPlaying = !isPlaying },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(text = if (isPlaying) "PAUSE" else "RESUME", color = Color.Black)
            }
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(text = "CANCEL", color = Color.Black)
            }
        }
    }
}


@Preview
@Composable
private fun TimerPlayPreview() {
    TimerPlayScreen("모프 실습 완성하기") {  ->  }
}
