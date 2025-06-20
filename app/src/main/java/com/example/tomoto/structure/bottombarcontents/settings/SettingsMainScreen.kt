package com.example.tomoto.structure.bottombarcontents.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    // 토마토 색상 팔레트
    val tomatoRed = Color(0xFFE74C3C)
    val tomatoOrange = Color(0xFFFF6B47)
    val creamWhite = Color(0xFFFFF8E7)
    val warmBrown = Color(0xFF8B4513)
    val lightTomato = Color(0xFFFFDDD8)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(creamWhite)
            .padding(16.dp)
    ) {
        SettingsUserInfo(
            userName = tomotoViewModel.userName,
            userLevel = tomotoViewModel.userLevel,
            onClick = { navController.navigate("UserInfo") }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 설정 리스트
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SettingsItem(title = "음악 설정", icon = "🎵") {
                navController.navigate("MusicList")
            }

            SettingsItem(title = "도전 과제", icon = "🏆") {
                navController.navigate("ChallengeList")
            }

            Button(
                onClick = { tomotoViewModel.gainXp(50) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = tomatoRed,
                    contentColor = Color.White
                )
            ) {
                Text(
                    "xp 얻기",
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
