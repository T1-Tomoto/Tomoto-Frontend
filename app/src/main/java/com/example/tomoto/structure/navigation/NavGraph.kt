package com.example.tomoto.structure.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tomoto.structure.model.Routes
import com.example.tomoto.structure.bottombarcontents.rank.Rank
import com.example.tomoto.structure.bottombarcontents.settings.ChallengeListScreen
import com.example.tomoto.structure.bottombarcontents.settings.MusicListScreen
import com.example.tomoto.structure.bottombarcontents.settings.Settings
import com.example.tomoto.structure.bottombarcontents.settings.UserInfoScreen
import com.example.tomoto.structure.bottombarcontents.timer.Timer
import com.example.tomoto.structure.bottombarcontents.todolist.TodoList
import com.example.tomoto.structure.viewmodel.TomotoViewModel

@Composable
fun NavGraph(navController: NavHostController, tomotoViewModel: TomotoViewModel) {
    NavHost(
        navController = navController,
        startDestination = Routes.Timer.route
    ) {
        composable(Routes.Timer.route) { Timer(tomotoViewModel = tomotoViewModel) }
        composable(Routes.TodoList.route) { TodoList(tomotoViewModel = tomotoViewModel) }
        composable(Routes.Rank.route) { Rank() }
        composable(Routes.Settings.route) {
            Settings(
                tomotoViewModel = tomotoViewModel,
                navController = navController
            )
        }
        composable(Routes.UserInfo.route) { UserInfoScreen(navController) }
        composable(Routes.MusicList.route) { MusicListScreen() }
        composable(Routes.ChallengeList.route) { ChallengeListScreen() }
    }
}