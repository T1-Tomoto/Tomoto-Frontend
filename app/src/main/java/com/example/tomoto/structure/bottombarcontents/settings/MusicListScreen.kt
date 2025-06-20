package com.example.tomoto.structure.bottombarcontents.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tomoto.structure.bottombarcontents.settings.uiconponents.AddMusicDialog
import com.example.tomoto.structure.bottombarcontents.settings.uiconponents.MusicCard
import com.example.tomoto.structure.bottombarcontents.settings.uiconponents.UserInfoTopAppBar
import com.example.tomoto.structure.datastructures.TomotoViewModel
import kotlinx.coroutines.flow.forEach

@Composable
fun MusicListScreen(navController : NavHostController, tomotoViewModel:TomotoViewModel) {
    // 토마토 색상 팔레트
    val tomatoRed = Color(0xFFE74C3C)
    val tomatoOrange = Color(0xFFFF6B47)
    val creamWhite = Color(0xFFFFF8E7)
    val warmBrown = Color(0xFF8B4513)
    val lightTomato = Color(0xFFFFDDD8)

    val musicList by tomotoViewModel.musicList.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var isEditMode by remember { mutableStateOf(false) }
    var editingUrl by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        tomotoViewModel.fetchMusicList()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    isEditMode = false
                    showDialog = true
                },
                containerColor = tomatoRed,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Music")
            }
        },
        topBar = {
            UserInfoTopAppBar(
                showBackButton = true,
                onBackClick = { navController.popBackStack() },
                titleText = "Youtube 음악 목록"
            )
        },
        containerColor = creamWhite
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(creamWhite)
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (musicList.isEmpty()) {
                Text(
                    "추가된 음악이 없습니다.",
                    color = warmBrown,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.offset(y = (-40).dp)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    musicList.forEach { url ->
                        MusicCard(
                            url = url,
                            onDelete = {
                                tomotoViewModel.removeMusicUrl(url)
                            },
                            onEdit = {
                                editingUrl = url
                                isEditMode = true
                                showDialog = true
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        if (showDialog) {
            AddMusicDialog(
                isEditMode = isEditMode,
                initialValue = editingUrl,
                onDismiss = { showDialog = false },
                onConfirm = { newUrl ->
                    if (isEditMode) {
                        tomotoViewModel.editMusicUrl(editingUrl, newUrl)
                    } else {
                        tomotoViewModel.addMusicUrl(newUrl)
                    }
                    showDialog = false
                }
            )
        }
    }
}