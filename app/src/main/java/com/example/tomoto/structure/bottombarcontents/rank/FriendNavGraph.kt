package com.example.tomoto.structure.bottombarcontents.rank

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tomoto.structure.datastructures.FriendsViewModel

@Composable
fun FriendNavGraph(friendsViewModel: FriendsViewModel = viewModel() , navController: NavHostController) {
    NavHost(navController = navController, startDestination = "friendRanking") {
        composable("friendRanking") {
            FriendRankingScreen(friendsViewModel)
        }
        composable("globalRanking") {
            GlobalRankingScreen()
        }
    }
}
