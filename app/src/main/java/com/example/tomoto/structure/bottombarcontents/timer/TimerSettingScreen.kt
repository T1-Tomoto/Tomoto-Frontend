package com.example.tomoto.structure.bottombarcontents.timer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
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

@Composable
fun TimerSettingScreen(
    onStartFocus: (String, Int) -> Unit
) {
    var task by remember { mutableStateOf("") }
    var pomoCount by remember { mutableStateOf(5) }
    val highlightColor = Color(0xFFFFDEDE)
    val isFocused = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
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
                        color = Color.Black,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = strokeWidth
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            if (task.isEmpty() && !isFocused.value) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "집중하고 싶은 일을 적어주세요!",
                        color = Color.Gray,
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
                    color = Color.Black,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp)
                    .onFocusChanged {
                        isFocused.value = it.isFocused
                    },
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

        Text("몇 뽀모도로 집중할 건가요?")
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { if (pomoCount > 1) pomoCount-- },
                modifier = Modifier.size(80.dp)
            ) {
                Text("-", fontSize = 60.sp)
            }

            Text(
                text = pomoCount.toString(),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            IconButton(
                onClick = { if (pomoCount < 10) pomoCount++ },
                modifier = Modifier.size(80.dp)
            ) {
                Text("+", fontSize = 32.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (task.isNotBlank()) onStartFocus(task, pomoCount)
            },
            colors = ButtonDefaults.buttonColors(containerColor = highlightColor)
        ) {
            Text(
                text = "집중 시작",
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = buildAnnotatedString {
                append("오늘 총 ")
                withStyle(style = SpanStyle(color = highlightColor, fontWeight = FontWeight.Bold)) {
                    append("7")
                }
                append(" 뽀모도로로 집중하셨네요!")
            },
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Preview
@Composable
private fun TimerSettingPreview() {
    TimerSettingScreen { _, _ -> }
}
