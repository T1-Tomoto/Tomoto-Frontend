package com.example.tomoto.structure.bottombarcontents

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tomoto.R
import com.example.tomoto.structure.datastructures.AuthViewModel
import com.example.tomoto.structure.datastructures.TomotoViewModel
import com.example.tomoto.structure.model.Routes
import com.example.tomoto.structure.navigation.BottomNavigationBar
import com.example.tomoto.structure.navigation.NavGraph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(tomotoViewModel: TomotoViewModel = viewModel(),
               authViewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val fullScreenRoutes = listOf(
        Routes.UserInfo.route,
        Routes.MusicList.route,
        Routes.ChallengeList.route,
        Routes.Login.route,
        Routes.Signup.route
    )

    val isFullScreen = currentRoute in fullScreenRoutes
    val context = LocalContext.current

    // 토마토 색상 팔레트
    val tomatoRed = Color(0xFFFF6347)
    val creamWhite = Color(0xFFFFF8E7)

    LaunchedEffect(Unit) {
        tomotoViewModel.initializeChallenges(context)
    }
    Scaffold(
        topBar = {
            if (!isFullScreen) {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Absolute.Left,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.tomoto_logo), // 로고 이미지
                                contentDescription = "Tomoto Logo",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = creamWhite
                    )
                )
            }
        },

        bottomBar = {
            if (!isFullScreen) {
                BottomNavigationBar(navController)
            }
        }
    ) { contentPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .background(creamWhite)
            .padding(contentPadding)) {
            NavGraph(navController, tomotoViewModel,authViewModel)
        }
    }
}