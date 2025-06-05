package com.example.tomoto.structure.bottombarcontents.rank

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.team_project_clock.friend.FriendRankingScreen
import com.example.team_project_clock.friend.GlobalRankingScreen

@Composable
fun FriendNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "friendRanking") {
        composable("friendRanking") {
            FriendRankingScreen()
        }
        composable("globalRanking") {
            GlobalRankingScreen()
        }
    }
}
