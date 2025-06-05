package com.example.tomoto.structure.bottombarcontents.rank

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.items
import com.example.team_project_clock.friend.Friend
import com.example.team_project_clock.R

@Composable
fun FriendRankingScreen() {
    val friends = listOf(
        Friend(1, "현수", true, 5, 10, R.drawable.baseline_person_24),
        Friend(2, "민수", false, 2, 8, R.drawable.baseline_person_24),
        Friend(3, "지영", true, 3, 9, R.drawable.baseline_person_24),
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("친구 검색") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
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
                            if (friend.isActive) {
                                Text("●", color = Color.Green, fontSize = 12.sp)
                            }
                        }
                        Text("오늘 ${friend.pomodoroCount} 뽀모도로 공부 중!")
                    }

                    Button(onClick = { /* 삭제 로직 */ }) {
                        Text("삭제")
                    }
                }
            }
        }
    }
}
