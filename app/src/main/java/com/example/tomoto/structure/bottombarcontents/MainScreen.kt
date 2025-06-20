package com.example.tomoto.structure.bottombarcontents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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

    LaunchedEffect(Unit) {
        tomotoViewModel.initializeChallenges(context)
    }
    Scaffold(
        topBar = {
            if (!isFullScreen) {
                TopAppBar(title = { Text("Tomoto") })
            }   },

   bottomBar = {
            if (!isFullScreen) {
                BottomNavigationBar(navController)
            }
        }
    ) { contentPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)) {
            NavGraph(navController, tomotoViewModel,authViewModel)
        }
    }
}
