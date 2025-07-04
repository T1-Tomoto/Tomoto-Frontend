package com.example.tomoto.structure.bottombarcontents.timer

import android.content.Context
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay
import com.example.tomoto.R
import com.example.tomoto.structure.datastructures.TomotoViewModel
import kotlinx.coroutines.flow.firstOrNull

@Composable
fun TimerPlayScreen(
    taskName: String = "",
    pomoCount: Int = 0,
    onCancel: () -> Unit,
    viewModel: TomotoViewModel,
    context: Context,
    isExampleMode: Boolean = false
) {
    val realFocusTime = 25 * 60
    val realRestTime = 5 * 60
    val realLongRestTime = 30 * 60
    val exampleTime = 6

    val focusTime = if (isExampleMode) exampleTime else realFocusTime
    val restTime = if (isExampleMode) exampleTime else realRestTime
    val longRestTime = if (isExampleMode) exampleTime else realLongRestTime

    var isPlaying by remember { mutableStateOf(true) }
    var showVolume by remember { mutableStateOf(true) }
    var currentPhase by remember { mutableStateOf("FOCUS") }
    var currentPomoIndex by remember { mutableStateOf(0) }
    var timer by remember { mutableStateOf(focusTime) }
    var focusStreak by remember { mutableStateOf(0) }

    // WebView 참조를 위한 remember
    var webView by remember { mutableStateOf<WebView?>(null) }

    val musicListState by viewModel.musicList.collectAsState()
    val musicUrl = musicListState.firstOrNull() ?: ""

    // 토마토 색상 팔레트
    val tomatoRed = Color(0xFFE74C3C)
    val tomatoOrange = Color(0xFFFF6B47)
    val creamWhite = Color(0xFFFFF8E7)
    val warmBrown = Color(0xFF8B4513)

    LaunchedEffect(isPlaying, currentPhase, currentPomoIndex) {
        while (isPlaying && currentPomoIndex < pomoCount) {
            delay(1000)
            timer--

            if (timer <= 0) {
                if (currentPhase == "FOCUS") {
                    focusStreak++
                    viewModel.incrementPomodoroAndEvaluate(context, focusStreak)
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
//                    focusStreak = 0
                }
            }
        }
    }

    val minutes = timer / 60
    val seconds = timer % 60

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(creamWhite)
    ) {
        // 타이머 UI
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // 음소거 버튼과 유튜브 영상을 겹쳐서 배치
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopEnd
            ) {
                // 투명한 유튜브 영상 (항상 렌더링)
                if (musicUrl.isNotBlank()) {
                    AndroidView(
                        modifier = Modifier
                            .width(48.dp)
                            .height(48.dp)
                            .alpha(0f),
                        factory = {
                            WebView(it).apply {
                                settings.javaScriptEnabled = true
                                settings.mediaPlaybackRequiresUserGesture = false
                                loadUrl(musicUrl.replace("watch?v=", "embed/") + "?autoplay=1&mute=${if (showVolume) 0 else 1}")
                                webView = this
                            }
                        }
                    )
                }

                // 음소거 버튼 (위에 겹쳐서 배치)
                IconButton(
                    onClick = {
                        showVolume = !showVolume
                        webView?.let { web ->
                            web.evaluateJavascript(
                                """
                                var video = document.querySelector('video');
                                if (video) {
                                    if (video.paused) {
                                        video.play();
                                    } else {
                                        video.pause();
                                    }
                                }
                                """.trimIndent(),
                                null
                            )
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(
                            if (showVolume) R.drawable.baseline_volume_up_24 else R.drawable.baseline_volume_off_24
                        ),
                        contentDescription = "Toggle Sound",
                        tint = tomatoRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$taskName",
                    fontSize = 20.sp,
                    color = warmBrown,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Round ${currentPomoIndex + 1} of $pomoCount",
                    fontSize = 14.sp,
                    color = tomatoOrange
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = String.format("%02d:%02d", minutes, seconds),
                    fontSize = 36.sp,
                    color = tomatoRed,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (currentPhase == "FOCUS") "집중 시간" else "쉬는 시간",
                    fontSize = 16.sp,
                    color = warmBrown
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { isPlaying = !isPlaying },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = tomatoOrange,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = if (isPlaying) "PAUSE" else "RESUME")
                }
                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = warmBrown,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "CANCEL")
                }
            }
        }
    }
}

