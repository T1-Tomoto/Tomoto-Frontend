package com.example.tomoto.structure.bottombarcontents.timer

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tomoto.structure.datastructures.TomotoViewModel

@Composable
fun TimerNavGraph(viewModel: TomotoViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "setting") {
        composable("setting") {
            TimerSettingScreen(viewModel = viewModel) { taskName, pomoCount ->
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
            val name = backStackEntry.arguments?.getString("taskName") ?: ""
            val count = backStackEntry.arguments?.getInt("pomoCount") ?: 0
            TimerPlayScreen(
                taskName = name,
                pomoCount = count,
                onCancel = { navController.popBackStack() },
                viewModel = viewModel,
                context = LocalContext.current
            )
        }
    }
}