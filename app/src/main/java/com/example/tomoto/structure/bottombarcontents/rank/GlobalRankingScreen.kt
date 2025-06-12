package com.example.tomoto.structure.bottombarcontents.rank

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tomoto.R
import com.example.tomoto.structure.datastructures.FriendsViewModel

@Composable
fun GlobalRankingScreen(friendsViewModel: FriendsViewModel = viewModel()) {

    val userRanking by friendsViewModel.userRanking.collectAsState()

    LaunchedEffect(Unit) {
        Log.d("CheckEffect", "LaunchedEffect 진입")
        friendsViewModel.fetchAllUserRanking()
    }
    Log.i("랭킹 결과",userRanking.toString())

    val users = listOf(
        Friend("현수", 5, 10, R.drawable.baseline_person_24),
        Friend("민수", 4, 9, R.drawable.baseline_person_24),
        Friend("지영", 3, 8, R.drawable.baseline_person_24),
        Friend("도윤", 2, 7, R.drawable.baseline_person_24),
        Friend("영희", 1, 6, R.drawable.baseline_person_24)
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
