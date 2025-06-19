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

    val userRanking by friendsViewModel.userRanking.collectAsState()

    LaunchedEffect(Unit) {
        Log.d("CheckEffect", "LaunchedEffect 진입")
        friendsViewModel.fetchAllUserRanking()
    }
    Log.i("랭킹 결과", userRanking.toString()) // 실제 데이터가 여기에 찍힘

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), // 좌우 패딩만 적용
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 상위 랭커 섹션
        Spacer(modifier = Modifier.height(16.dp)) // 상단 여백 추가

        // userRanking이 충분히 있을 때만 상위 3명 표시
        if (userRanking.isNotEmpty()) {
            val topThree = userRanking.take(3) // 상위 3명 가져오기

            // 1, 2, 3위 사용자 (데이터가 없으면 null)
            val firstPlaceUser = topThree.getOrNull(0)
            val secondPlaceUser = topThree.getOrNull(1)
            val thirdPlaceUser = topThree.getOrNull(2)

            Row(
                modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max), // Row 내의 Column들이 같은 높이를 가지도록
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom // 하단 정렬로 텍스트 위치 맞춤
            ) {
                // 2위 표시
                if (secondPlaceUser != null) {
                    RankingPodiumItem(
                        rank = 2,
                        user = secondPlaceUser,
                        imageRes = R.drawable.baseline_person_24, // 임시 이미지
                        size = 80.dp, // 2위 이미지 크기
                        fontSize = 14.sp
                    )
                } else {
                    Spacer(Modifier.weight(1f)) // 2위 자리 비워두기 (레이아웃 균형)
                }

                // 1위 표시
                if (firstPlaceUser != null) {
                    RankingPodiumItem(
                        rank = 1,
                        user = firstPlaceUser,
                        imageRes = R.drawable.baseline_person_24, // 임시 이미지
                        size = 100.dp, // 1위 이미지 크기
                        fontSize = 16.sp
                    )
                } else {
                    Spacer(Modifier.weight(1f)) // 1위 자리 비워두기 (레이아웃 균형)
                }

                // 3위 표시
                if (thirdPlaceUser != null) {
                    RankingPodiumItem(
                        rank = 3,
                        user = thirdPlaceUser,
                        imageRes = R.drawable.baseline_person_24, // 임시 이미지
                        size = 80.dp, // 3위 이미지 크기
                        fontSize = 14.sp
                    )
                } else {
                    Spacer(Modifier.weight(1f)) // 3위 자리 비워두기 (레이아웃 균형)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Divider() // 구분선
        } else {
            // 랭킹 데이터가 없을 경우
            Text("랭킹 정보를 불러오는 중이거나, 랭킹 데이터가 없습니다.", modifier = Modifier.padding(16.dp))
        }

        // 나머지 랭커 리스트
        LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) { // 남은 공간 차지
            itemsIndexed(userRanking.drop(3)) { index, user -> // 4위부터 표시
                RankingListItem(
                    rank = index + 4, // 0번째 인덱스는 4위
                    user = user,
                    imageRes = R.drawable.baseline_person_24 // 임시 이미지
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.wrapContentHeight(), // 내용에 따라 높이 조절
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1위, 2위, 3위는 Text가 아닌 아이콘 등으로 꾸밀 수 있습니다.
        // 현재는 Text를 사용합니다.
        Text(
            "${rank}위",
            fontWeight = FontWeight.Bold,
            fontSize = fontSize,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.size(size)
        )
        Text(
            user.nickname,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            "Lv.${user.level}",
            fontSize = fontSize.times(0.8)
        )
    }
}

// LazyColumn에서 각 랭커를 표시하기 위한 Composable
@Composable
fun RankingListItem(
    rank: Int,
    user: AllUserRankRes,
    imageRes: Int,
    modifier: Modifier = Modifier
) {
    Column {
        Row(
            modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "${rank}위",
                modifier = Modifier.width(40.dp),
                fontWeight = FontWeight.Bold
            )
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(user.nickname, modifier = Modifier.weight(1f))
            Text("Lv.${user.level}", fontWeight = FontWeight.Medium)
        }
        Divider() // 각 아이템 하단에 구분선
    }
}
