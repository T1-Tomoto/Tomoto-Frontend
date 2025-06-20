package com.example.tomoto.structure.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tomoto.structure.auth.LoginScreen
import com.example.tomoto.structure.auth.SignupScreen
import com.example.tomoto.structure.auth.token.TokenManager
import com.example.tomoto.structure.bottombarcontents.rank.MainScreenFriend
import com.example.tomoto.structure.bottombarcontents.settings.ChallengeListScreen
import com.example.tomoto.structure.bottombarcontents.settings.MusicListScreen
import com.example.tomoto.structure.bottombarcontents.settings.Settings
import com.example.tomoto.structure.bottombarcontents.settings.UserInfoScreen
import com.example.tomoto.structure.model.Routes
import com.example.tomoto.structure.bottombarcontents.timer.TimerNavGraph
import com.example.tomoto.structure.bottombarcontents.todolist.ToDoScreenWithCalendarComposable2
import com.example.tomoto.structure.datastructures.AuthViewModel
import com.example.tomoto.structure.datastructures.TomotoViewModel


@Composable
fun NavGraph(
    navController: NavHostController,
    tomotoViewModel: TomotoViewModel,
    authViewModel: AuthViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Login.route
    ) {
        composable(Routes.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginClick = { id, pw ->
                    authViewModel.login(id, pw) { accessToken ->
                        // 로그인 성공 시 실행될 코드
                        TokenManager.setAccessToken(accessToken)
                        Log.d("NavGraph", "로그인 성공! 토큰 저장됨: $accessToken")

                        navController.navigate(Routes.Timer.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                        tomotoViewModel.loadDataAfterLogin()
                    }
                },
                onSignupClick = {
                    navController.navigate(Routes.Signup.route)
                },
                errorMessage = authViewModel.errorMessage // 에러 메시지 전달
            )
        }

        composable(Routes.Signup.route) {
            SignupScreen(
                viewModel = authViewModel,
                onSignupClick = { id, pw, nickname ->
                    authViewModel.signup(id, pw, nickname) { accessToken ->
                        // 회원가입 성공 시 실행될 코드
                        TokenManager.setAccessToken(accessToken)
                        Log.d("NavGraph", "회원가입 성공! 토큰 저장됨: $accessToken")

                        navController.navigate(Routes.Timer.route) {
                            popUpTo(Routes.Signup.route) { inclusive = true }
                        }
                        tomotoViewModel.loadDataAfterLogin()
                    }
                },
                onBackToLogin = {
                    authViewModel.clearErrorMessage()
                    navController.popBackStack()
                },
                errorMessage = authViewModel.errorMessage
            )
        }

        composable(Routes.Timer.route) { TimerNavGraph(viewModel = tomotoViewModel) }
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