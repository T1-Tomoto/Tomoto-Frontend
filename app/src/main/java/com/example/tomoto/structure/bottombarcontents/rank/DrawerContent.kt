package com.example.tomoto.structure.bottombarcontents.rank

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun DrawerContent(
    navController: NavHostController,
    closeDrawer: () -> Unit
) {
    // 토마토 색상 팔레트
    val tomatoRed = Color(0xFFE74C3C)
    val tomatoOrange = Color(0xFFFF6B47)
    val creamWhite = Color(0xFFFFF8E7)
    val warmBrown = Color(0xFF8B4513)
    val lightTomato = Color(0xFFFFDDD8)

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(creamWhite)
            .padding(16.dp)
    ) {
        Text(
            text = "친구 랭킹",
            color = warmBrown,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(lightTomato)
                .padding(16.dp)
                .clickable {
                    navController.navigate("friendRanking")
                    closeDrawer()
                }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "전체 랭킹",
            color = warmBrown,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(lightTomato)
                .padding(16.dp)
                .clickable {
                    navController.navigate("globalRanking")
                    closeDrawer()
                }
        )
    }
}