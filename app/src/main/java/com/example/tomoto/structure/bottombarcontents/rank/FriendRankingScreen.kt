package com.example.tomoto.structure.bottombarcontents.rank

import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tomoto.R
import com.example.tomoto.structure.datastructures.FriendsViewModel

@Composable
fun FriendRankingScreen(friendsViewModel: FriendsViewModel= viewModel()) {

    val friendsRanking by friendsViewModel.friendsRanking.collectAsState()

    LaunchedEffect(Unit) {
        Log.d("CheckEffect", "LaunchedEffect 진입")
        friendsViewModel.fetchFriendsRanking()
    }
    Log.i("친구 랭킹 결과",friendsRanking.toString())

    val friends = listOf(
        Friend(1, "현수", true, 5, 10, R.drawable.baseline_person_24),
        Friend(2, "민수", false, 2, 8, R.drawable.baseline_person_24),
        Friend(3, "지영", true, 3, 9, R.drawable.baseline_person_24),
    )

    var nicknameInput by remember { mutableStateOf("") }
    var friendToDelete by remember { mutableStateOf<Friend?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadFriendsFromDb()
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // Snackbar 영역
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(friends) { friend ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = friend.imageRes),
                            contentDescription = "Profile",
                            modifier = Modifier.size(48.dp).clip(CircleShape)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(friend.nickname, fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text("오늘 ${friend.pomodoroCount} 뽀모도로 공부 중!")
                        }

                        Button(onClick = {
                            friendToDelete = friend
                        }) {
                            Text("삭제")
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = nicknameInput,
                onValueChange = { nicknameInput = it },
                label = { Text("닉네임으로 친구 추가") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )

            Button(
                onClick = {
                    coroutineScope.launch {
                        val result = viewModel.tryAddFriend(nicknameInput)
                        snackbarHostState.showSnackbar(result)
                        nicknameInput = ""
                    }
                },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text("검색")
            }
        }

        // 삭제 다이얼로그
        if (friendToDelete != null) {
            AlertDialog(
                onDismissRequest = { friendToDelete = null },
                title = { Text("친구 삭제") },
                text = { Text("${friendToDelete!!.nickname}님을 삭제하시겠습니까?") },
                confirmButton = {
                    TextButton(onClick = {
                        coroutineScope.launch {
                            viewModel.deleteFriend(friendToDelete!!.nickname)
                            snackbarHostState.showSnackbar("${friendToDelete!!.nickname}님이 삭제되었습니다.")
                            friendToDelete = null
                        }
                    }) {
                        Text("삭제")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { friendToDelete = null }) {
                        Text("취소")
                    }
                }
            )
        }
    }
}
