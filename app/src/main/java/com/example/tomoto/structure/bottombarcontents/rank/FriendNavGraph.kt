package com.example.tomoto.structure.bottombarcontents.rank

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

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
