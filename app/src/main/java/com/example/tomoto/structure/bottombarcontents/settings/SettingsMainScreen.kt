package com.example.tomoto.structure.bottombarcontents.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tomoto.structure.bottombarcontents.settings.uiconponents.SettingsItem
import com.example.tomoto.structure.bottombarcontents.settings.uiconponents.SettingsUserInfo
import com.example.tomoto.structure.datastructures.TomotoViewModel

@Composable
fun Settings(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    tomotoViewModel: TomotoViewModel
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SettingsUserInfo(UserName = tomotoViewModel.userName, onClick = { navController.navigate("UserInfo") })

        Spacer(modifier = Modifier.height(24.dp))

        // 설정 리스트
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SettingsItem(title = "음악 설정", icon = "🎵") {
                navController.navigate("MusicList")
            }

            SettingsItem(title = "도전 과제", icon = "🏆") {
                navController.navigate("ChallengeList")
            }
        }
    }
}
