package com.example.tomoto.structure.bottombarcontents.rank

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tomoto.R
import com.example.tomoto.structure.datastructures.TomotoViewModel
import com.example.tomoto.structure.data.dto.request.AddFriendReq
import kotlinx.coroutines.launch

@Composable
fun FriendRankingScreen(viewModel: TomotoViewModel = viewModel()) {
    // 토마토 색상 팔레트
    val tomatoRed = Color(0xFFE74C3C)
    val tomatoOrange = Color(0xFFFF6B47)
    val creamWhite = Color(0xFFFFF8E7)
    val warmBrown = Color(0xFF8B4513)
    val lightTomato = Color(0xFFFFDDD8)

    val friendsRanking by viewModel.friendsRanking.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchFriendsRanking()
    }

    var nicknameInput by remember { mutableStateOf("") }
    var friendToDelete by remember { mutableStateOf<Friend?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(creamWhite)
    ) {
        // Snackbar 영역
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // 전체를 Column으로 변경
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // LazyColumn - weight 제거하고 fillMaxSize 사용하지 않음
            LazyColumn(
                modifier = Modifier.weight(1f), // 남은 공간 차지
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(friendsRanking) { friend ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = friend.imageRes),
                                contentDescription = "Profile",
                                modifier = Modifier.size(48.dp).clip(CircleShape)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    friend.nickname,
                                    fontSize = 18.sp,
                                    color = warmBrown,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    "오늘 ${friend.pomodoroCount} 뽀모도로 공부 중!",
                                    color = tomatoOrange,
                                    fontSize = 14.sp
                                )
                            }

                            Button(
                                onClick = { friendToDelete = friend },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = tomatoRed,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("삭제", fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            // 친구 추가 입력창을 Column 하단에 배치
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                OutlinedTextField(
                    value = nicknameInput,
                    onValueChange = { nicknameInput = it },
                    label = { Text("닉네임으로 친구 추가", color = warmBrown) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = tomatoOrange) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = tomatoRed,
                        unfocusedBorderColor = tomatoOrange,
                        focusedLabelColor = tomatoRed,
                        cursorColor = tomatoRed
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Button(
                    onClick = {
                        coroutineScope.launch {
                            val dto = AddFriendReq(friendName = nicknameInput)
                            viewModel.fetchAddFriend(dto)
                            snackbarHostState.showSnackbar(
                                message = "${dto.friendName}님을 친구로 추가했어요!",
                                duration = SnackbarDuration.Short
                            )
                            nicknameInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = tomatoRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("검색", fontWeight = FontWeight.Medium)
                }
            }
        }

        // 삭제 다이얼로그
        if (friendToDelete != null) {
            AlertDialog(
                onDismissRequest = { friendToDelete = null },
                title = { Text("친구 삭제", color = warmBrown, fontWeight = FontWeight.Bold) },
                text = { Text("${friendToDelete!!.nickname}님을 삭제하시겠습니까?", color = warmBrown) },
                confirmButton = {
                    TextButton(onClick = {
                        coroutineScope.launch {
                            viewModel.fetchDeleteFriend(friendToDelete!!.nickname)
                            snackbarHostState.showSnackbar("${friendToDelete!!.nickname}님이 삭제되었습니다.")
                            friendToDelete = null
                        }
                    }) {
                        Text("삭제", color = tomatoRed, fontWeight = FontWeight.Medium)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { friendToDelete = null }) {
                        Text("취소", color = warmBrown, fontWeight = FontWeight.Medium)
                    }
                },
                containerColor = creamWhite
            )
        }
    }
}