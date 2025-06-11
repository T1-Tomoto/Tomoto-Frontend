package com.example.tomoto.structure.bottombarcontents.settings

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
    val sortedDaily = tomotoViewModel.dailyChallenges.sortedBy { it.isCompleted }
    val sortedPermanent = tomotoViewModel.permanentChallenges.sortedBy { it.isCompleted }

    Scaffold(
        topBar = {
            UserInfoTopAppBar(
                showBackButton = true,
                onBackClick = { navController.popBackStack() },
                titleText = "도전 과제"
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            item {
                Text("📅 일일 도전과제", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(sortedDaily) { challenge ->
                ChallengeCard(challenge)
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text("🏆 영구 도전과제", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(sortedPermanent) { challenge ->
                ChallengeCard(challenge)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
