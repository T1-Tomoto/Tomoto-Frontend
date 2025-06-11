package com.example.tomoto.structure.bottombarcontents.timer

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun TimerNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "setting") {
        composable("setting") {
            TimerSettingScreen { taskName, pomoCount ->
                navController.navigate("timer/$taskName/$pomoCount")
            }
        }

        composable(
            route = "timer/{taskName}/{pomoCount}",
            arguments = listOf(
                navArgument("taskName") { type = NavType.StringType },
                navArgument("pomoCount") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val task = backStackEntry.arguments?.getString("taskName") ?: ""
            val count = backStackEntry.arguments?.getInt("pomoCount") ?: 0
            TimerPlayScreen(
                taskName = task,
                pomoCount = count,
                onCancel = { navController.popBackStack() }
            )
        }
    }
}
