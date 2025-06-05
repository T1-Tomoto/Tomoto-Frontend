package com.example.tomoto.structure.bottombarcontents.rank

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.team_project_clock.friend.Friend
import com.example.team_project_clock.R

@Composable
fun GlobalRankingScreen() {
    val users = listOf(
        Friend(1, "현수", true, 5, 10, R.drawable.baseline_person_24),
        Friend(2, "민수", false, 4, 9, R.drawable.baseline_person_24),
        Friend(3, "지영", true, 3, 8, R.drawable.baseline_person_24),
        Friend(4, "도윤", true, 2, 7, R.drawable.baseline_person_24),
        Friend(5, "영희", false, 1, 6, R.drawable.baseline_person_24)
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 상위 3명
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("2위", fontWeight = FontWeight.Bold)
                Image(painter = painterResource(id = users[1].imageRes), contentDescription = null, modifier = Modifier.size(60.dp))
                Text(users[1].nickname)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("1위", fontWeight = FontWeight.Bold)
                Image(painter = painterResource(id = users[0].imageRes), contentDescription = null, modifier = Modifier.size(80.dp))
                Text(users[0].nickname)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("3위", fontWeight = FontWeight.Bold)
                Image(painter = painterResource(id = users[2].imageRes), contentDescription = null, modifier = Modifier.size(60.dp))
                Text(users[2].nickname)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider()

        LazyColumn {
            itemsIndexed(users.drop(3)) { index, user ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${index + 4}위", modifier = Modifier.width(40.dp))
                    Image(painter = painterResource(id = user.imageRes), contentDescription = null, modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(user.nickname, modifier = Modifier.weight(1f))
                    Text("Lv.${user.level}")
                }
                Divider()
            }
        }
    }
}
