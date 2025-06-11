package com.example.tomoto.structure.bottombarcontents.timer

import android.content.Context
import android.webkit.WebView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay
import com.example.tomoto.R
import com.example.tomoto.structure.datastructures.TomotoViewModel

@Composable
fun TimerPlayScreen(
    taskName: String = "",
    pomoCount: Int = 0,
    onCancel: () -> Unit,
    viewModel: TomotoViewModel,
    context: Context
) {
    var isPlaying by remember { mutableStateOf(true) }
    var showVolume by remember { mutableStateOf(true) }

    val focusTime = 1 * 60 / 10 // 실제로 25분
    val restTime = 1 * 60 / 10 // 실제로 5분
    val longRestTime = 1 * 60 / 6 // 실제로 30분

    var currentPhase by remember { mutableStateOf("FOCUS") }
    var currentPomoIndex by remember { mutableStateOf(0) }
    var timer by remember { mutableStateOf(focusTime) }

    val musicUrl = viewModel.musicList.firstOrNull() ?: ""

    LaunchedEffect(isPlaying, currentPhase, currentPomoIndex) {
        while (isPlaying && currentPomoIndex < pomoCount) {
            delay(1000)
            timer--

            if (timer <= 0) {
                if (currentPhase == "FOCUS") {
                    viewModel.incrementPomodoroAndEvaluate(context)
                    currentPhase = "REST"
                    timer = if ((currentPomoIndex + 1) % 4 == 0) longRestTime else restTime
                } else {
                    currentPomoIndex++
                    if (currentPomoIndex < pomoCount) {
                        currentPhase = "FOCUS"
                        timer = focusTime
                    } else {
                        isPlaying = false
                        onCancel()
                    }
                }
            }
        }
    }

    val minutes = timer / 60
    val seconds = timer % 60

    Box(modifier = Modifier.fillMaxSize()) {
        // 유튜브 WebView (뒤에 배치)
        if (musicUrl.isNotBlank()) {
            AndroidView(
                modifier = Modifier.matchParentSize(),
                factory = {
                    WebView(it).apply {
                        settings.javaScriptEnabled = true
                        loadUrl(musicUrl.replace("watch?v=", "embed/") + "?autoplay=1&mute=${if (showVolume) 0 else 1}")
                    }
                }
            )
        }

        // 타이머 UI
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

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

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "$taskName", fontSize = 20.sp)
                Text(text = "Round ${currentPomoIndex + 1} of $pomoCount", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = String.format("%02d:%02d", minutes, seconds), fontSize = 36.sp)
                Text(text = if (currentPhase == "FOCUS") "집중 시간" else "쉬는 시간", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(48.dp))

            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { isPlaying = !isPlaying }) {
                    Text(text = if (isPlaying) "PAUSE" else "RESUME")
                }
                Button(onClick = onCancel) {
                    Text(text = "CANCEL")
                }
            }
        }
    }
}