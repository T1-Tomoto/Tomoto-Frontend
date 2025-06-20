package com.example.tomoto.structure.bottombarcontents.rank

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tomoto.R
import com.example.tomoto.structure.data.dto.response.AllUserRankRes
import com.example.tomoto.structure.datastructures.FriendsViewModel

@Composable
fun GlobalRankingScreen(friendsViewModel: FriendsViewModel = viewModel()) {

    // 토마토 색상 팔레트
    val tomatoRed = Color(0xFFE74C3C)
    val tomatoOrange = Color(0xFFFF6B47)
    val creamWhite = Color(0xFFFFF8E7)
    val warmBrown = Color(0xFF8B4513)
    val lightTomato = Color(0xFFFFDDD8)

    val userRanking by friendsViewModel.userRanking.collectAsState()

    LaunchedEffect(Unit) {
        Log.d("CheckEffect", "LaunchedEffect 진입")
        friendsViewModel.fetchAllUserRanking()
    }
    Log.i("랭킹 결과", userRanking.toString())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(creamWhite)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 상위 랭커 섹션
        Spacer(modifier = Modifier.height(16.dp))

        if (userRanking.isNotEmpty()) {
            val topThree = userRanking.take(3)

            val firstPlaceUser = topThree.getOrNull(0)
            val secondPlaceUser = topThree.getOrNull(1)
            val thirdPlaceUser = topThree.getOrNull(2)

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // 2위 표시
                    if (secondPlaceUser != null) {
                        RankingPodiumItem(
                            rank = 2,
                            user = secondPlaceUser,
                            imageRes = R.drawable.baseline_person_24,
                            size = 80.dp,
                            fontSize = 14.sp,
                            rankColor = tomatoOrange,
                            textColor = warmBrown
                        )
                    } else {
                        Spacer(Modifier.weight(1f))
                    }

                    // 1위 표시
                    if (firstPlaceUser != null) {
                        RankingPodiumItem(
                            rank = 1,
                            user = firstPlaceUser,
                            imageRes = R.drawable.baseline_person_24,
                            size = 100.dp,
                            fontSize = 16.sp,
                            rankColor = tomatoRed,
                            textColor = warmBrown
                        )
                    } else {
                        Spacer(Modifier.weight(1f))
                    }

                    // 3위 표시
                    if (thirdPlaceUser != null) {
                        RankingPodiumItem(
                            rank = 3,
                            user = thirdPlaceUser,
                            imageRes = R.drawable.baseline_person_24,
                            size = 80.dp,
                            fontSize = 14.sp,
                            rankColor = tomatoOrange,
                            textColor = warmBrown
                        )
                    } else {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = lightTomato, thickness = 2.dp)
        } else {
            Text(
                "랭킹 정보를 불러오는 중이거나, 랭킹 데이터가 없습니다.",
                color = warmBrown,
                modifier = Modifier.padding(16.dp)
            )
        }

        // 나머지 랭커 리스트
        LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
            itemsIndexed(userRanking.drop(3)) { index, user ->
                RankingListItem(
                    rank = index + 4,
                    user = user,
                    imageRes = R.drawable.baseline_person_24,
                    rankColor = tomatoRed,
                    textColor = warmBrown
                )
            }
        }
    }
}

// 상위 3명 표시를 위한 Composable
@Composable
fun RankingPodiumItem(
    rank: Int,
    user: AllUserRankRes,
    imageRes: Int,
    size: Dp,
    fontSize: TextUnit,
    rankColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "${rank}위",
            fontWeight = FontWeight.Bold,
            fontSize = fontSize,
            color = rankColor,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFFDDD8))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.size(size)
        )
        Text(
            user.nickname,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize,
            color = textColor,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            "Lv.${user.level}",
            fontSize = fontSize.times(0.8),
            color = Color(0xFFFF6B47)
        )
    }
}

// LazyColumn에서 각 랭커를 표시하기 위한 Composable
@Composable
fun RankingListItem(
    rank: Int,
    user: AllUserRankRes,
    imageRes: Int,
    rankColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Column {
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${rank}위",
                    modifier = Modifier.width(40.dp),
                    fontWeight = FontWeight.Bold,
                    color = rankColor
                )
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    user.nickname,
                    modifier = Modifier.weight(1f),
                    color = textColor,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    "Lv.${user.level}",
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFFF6B47)
                )
            }
        }
    }
}