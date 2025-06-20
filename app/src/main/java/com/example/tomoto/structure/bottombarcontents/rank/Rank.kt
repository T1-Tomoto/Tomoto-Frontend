package com.example.tomoto.structure.bottombarcontents.rank

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Rank(modifier: Modifier = Modifier) {
    // 토마토 색상 팔레트
    val tomatoRed = Color(0xFFE74C3C)
    val creamWhite = Color(0xFFFFF8E7)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(creamWhite)
    ) {
        Icon(
            imageVector = Icons.Default.ThumbUp,
            contentDescription = "Rank",
            tint = tomatoRed,
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.Center)
        )
    }
}