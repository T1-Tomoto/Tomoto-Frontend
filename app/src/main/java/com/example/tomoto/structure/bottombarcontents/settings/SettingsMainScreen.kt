package com.example.tomoto.structure.bottombarcontents.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tomoto.structure.bottombarcontents.settings.uiconponents.SettingsItem
import com.example.tomoto.structure.bottombarcontents.settings.uiconponents.SettingsUserInfo
import com.example.tomoto.structure.viewmodel.TomotoViewModel

@Composable
fun Settings(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    tomotoViewModel: TomotoViewModel
) {
    Column(modifier = Modifier
        .fillMaxSize()) {

        SettingsUserInfo(onClick = { navController.navigate("UserInfo") })

        Spacer(modifier = Modifier.height(24.dp))

        SettingsItem(title = "음악 설정") {
            navController.navigate("MusicList")
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.outline
        )

        SettingsItem(title = "도전 과제") {
            navController.navigate("ChallengeList")
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.outline
        )
    }
}
