package com.example.tomoto.structure.bottombarcontents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tomoto.structure.model.Routes
import com.example.tomoto.structure.navigation.BottomNavigationBar
import com.example.tomoto.structure.navigation.NavGraph
import com.example.tomoto.structure.datastructures.TomotoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(tomotoViewModel: TomotoViewModel = viewModel()) {
    val navController = rememberNavController()

    // 현재 라우트 감지
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // TopBar, BottomBar 없이 전체를 덮는 화면 경로
    val fullScreenRoutes = listOf(
        Routes.UserInfo.route,
        Routes.MusicList.route,
        Routes.ChallengeList.route
    )

    val isFullScreen = currentRoute in fullScreenRoutes
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        tomotoViewModel.initialize(context)
    }
    Scaffold(
        topBar = {
            if (!isFullScreen) {
                TopAppBar(title = { Text("Tomoto") })
            }
        },
        bottomBar = {
            if (!isFullScreen) {
                BottomNavigationBar(navController)
            }
        }
    ) { contentPadding ->
        Box() {
            NavGraph(navController, tomotoViewModel, paddingValues = contentPadding)
        }
    }
}
