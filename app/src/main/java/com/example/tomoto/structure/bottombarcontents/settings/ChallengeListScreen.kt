package com.example.tomoto.structure.bottombarcontents.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tomoto.structure.bottombarcontents.settings.uiconponents.ChallengeCard
import com.example.tomoto.structure.bottombarcontents.settings.uiconponents.UserInfoTopAppBar
import com.example.tomoto.structure.datastructures.TomotoViewModel

@Composable
fun ChallengeListScreen(
    navController: NavHostController,
    tomotoViewModel: TomotoViewModel
) {
    // 토마토 색상 팔레트
    val tomatoRed = Color(0xFFE74C3C)
    val tomatoOrange = Color(0xFFFF6B47)
    val creamWhite = Color(0xFFFFF8E7)
    val warmBrown = Color(0xFF8B4513)
    val lightTomato = Color(0xFFFFDDD8)

    val sortedDaily = tomotoViewModel.dailyChallenges.sortedBy { it.isCompleted }
    val sortedPermanent = tomotoViewModel.permanentChallenges.sortedBy { it.isCompleted }

    Scaffold(
        topBar = {
            UserInfoTopAppBar(
                showBackButton = true,
                onBackClick = { navController.popBackStack() },
                titleText = "도전 과제"
            )
        },
        containerColor = creamWhite
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .background(creamWhite)
                .padding(padding)
                .padding(16.dp)
        ) {
            item {
                Text(
                    "📅 일일 도전과제",
                    style = MaterialTheme.typography.titleMedium,
                    color = warmBrown,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(sortedDaily) { challenge ->
                ChallengeCard(challenge)
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "🏆 영구 도전과제",
                    style = MaterialTheme.typography.titleMedium,
                    color = warmBrown,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(sortedPermanent) { challenge ->
                ChallengeCard(challenge)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}