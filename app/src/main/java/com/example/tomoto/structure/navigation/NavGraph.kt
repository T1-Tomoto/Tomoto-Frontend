package com.example.tomoto.structure.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tomoto.structure.bottombarcontents.rank.FriendNavGraph
import com.example.tomoto.structure.bottombarcontents.rank.MainScreenFriend
import com.example.tomoto.structure.bottombarcontents.rank.Rank
import com.example.tomoto.structure.bottombarcontents.settings.ChallengeListScreen
import com.example.tomoto.structure.bottombarcontents.settings.MusicListScreen
import com.example.tomoto.structure.bottombarcontents.settings.Settings
import com.example.tomoto.structure.bottombarcontents.settings.UserInfoScreen
import com.example.tomoto.structure.bottombarcontents.timer.Timer
import com.example.tomoto.structure.bottombarcontents.timer.TimerNavGraph
import com.example.tomoto.structure.bottombarcontents.todolist.ToDoScreenWithCalendarComposable2
import com.example.tomoto.structure.datastructures.TomotoViewModel
import com.example.tomoto.structure.model.Routes


@Composable
fun NavGraph(navController: NavHostController, tomotoViewModel: TomotoViewModel) {
    NavHost(
        navController = navController,
        startDestination = Routes.Timer.route
    ) {
        composable(Routes.Timer.route) { TimerNavGraph() }
        composable(Routes.TodoList.route) { ToDoScreenWithCalendarComposable2(tomotoViewModel = tomotoViewModel) }
        composable(Routes.Rank.route) { MainScreenFriend() }
        composable(Routes.Settings.route) {
            Settings(
                tomotoViewModel = tomotoViewModel,
                navController = navController
            )
        }
        composable(Routes.UserInfo.route) { UserInfoScreen(navController, tomotoViewModel) }
        composable(Routes.MusicList.route) { MusicListScreen(navController, tomotoViewModel) }
        composable(Routes.ChallengeList.route) { ChallengeListScreen(navController, tomotoViewModel) }
    }
}