package com.example.tomoto.structure.bottombarcontents.settings

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tomoto.R
import com.example.tomoto.structure.bottombarcontents.settings.uiconponents.EditIntroduceDialog
import com.example.tomoto.structure.bottombarcontents.settings.uiconponents.UserInfoTopAppBar
import com.example.tomoto.structure.datastructures.TomotoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInfoScreen(
    navController: NavHostController, tomotoViewModel: TomotoViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    val userInfo by tomotoViewModel.userInfo.collectAsState()

    Log.i("유저정보", userInfo.toString())

    Scaffold(
        topBar = {
            UserInfoTopAppBar(
                showBackButton = true,
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Image(
                painter = painterResource(id = R.drawable.img1),
                contentDescription = "User Profile",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
                    .shadow(4.dp, CircleShape)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = tomotoViewModel.userName,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "레벨 : ${tomotoViewModel.userLevel.level}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF666666)
                )
            )

            Spacer(modifier = Modifier.height(28.dp))

            if (tomotoViewModel.totalPomodoro != null && tomotoViewModel.totalPomodoro > 0) {
                Card (
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Timer,
                            contentDescription = "총 뽀모도로 횟수 아이콘",
                            tint = Color(0xFF444444)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "현재까지 진행한 뽀모도로 횟수 : ${tomotoViewModel.totalPomodoro}회",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } else if (userInfo != null) {
                Text(
                    text = "아직 완료한 뽀모도로가 없어요. 첫 뽀모도로를 시작해보세요!",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                )
            } else {
                Text(
                    text = "뽀모도로 정보를 불러오는 중...",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDialog = true },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = tomotoViewModel.introduce,
                    modifier = Modifier.padding(20.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (showDialog) {
            EditIntroduceDialog(
                currentIntroduce = tomotoViewModel.introduce,
                onDismiss = { showDialog = false },
                onConfirm = { newIntro ->
                    tomotoViewModel.updateIntroduce(newIntro)
                    showDialog = false
                }
            )
        }
    }
}
