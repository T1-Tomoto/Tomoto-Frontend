package com.example.tomoto.structure.navigation

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tomoto.structure.auth.LoginScreen
import com.example.tomoto.structure.auth.SignupScreen
import com.example.tomoto.structure.auth.token.TokenManager
import com.example.tomoto.structure.bottombarcontents.rank.FriendNavGraph
import com.example.tomoto.structure.bottombarcontents.rank.MainScreenFriend
import com.example.tomoto.structure.bottombarcontents.rank.Rank
import com.example.tomoto.structure.bottombarcontents.settings.ChallengeListScreen
import com.example.tomoto.structure.bottombarcontents.settings.MusicListScreen
import com.example.tomoto.structure.bottombarcontents.settings.Settings
import com.example.tomoto.structure.bottombarcontents.settings.UserInfoScreen
import com.example.tomoto.structure.data.ServicePool
import com.example.tomoto.structure.data.dto.request.UserLoginReq
import com.example.tomoto.structure.data.dto.request.UserRegisterReq
import com.example.tomoto.structure.model.Routes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.tomoto.structure.bottombarcontents.timer.TimerNavGraph
import com.example.tomoto.structure.bottombarcontents.todolist.ToDoScreenWithCalendarComposable2
import com.example.tomoto.structure.datastructures.TomotoViewModel


@Composable
fun NavGraph(navController: NavHostController, tomotoViewModel: TomotoViewModel) {
    NavHost(
        navController = navController,
        startDestination = Routes.Login.route
    ) {

        composable(Routes.Login.route) {
            LoginScreen(
                onLoginClick = { id, pw ->
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val res = ServicePool.userService.login(
                                UserLoginReq(id, pw)
                            )
                            Log.d("login", "accessToken = ${res.accessToken}")

                            // 토큰 저장
                            TokenManager.setAccessToken(res.accessToken)

                            // 다음 화면으로 이동
                            withContext(Dispatchers.Main) {
                                navController.navigate(Routes.Timer.route) {
                                    popUpTo(Routes.Login.route) { inclusive = true }
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("login", "로그인 실패: ${e.message}")
                        }
                    }
                },
                onSignupClick = {
                    navController.navigate(Routes.Signup.route)
                }
            )
        }

        composable(Routes.Signup.route) {
            SignupScreen(
                onSignupClick = { id, pw, nickname ->
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val res = ServicePool.userService.signup(
                                UserRegisterReq(id, pw, nickname)
                            )
                            Log.d("signup", "토큰: ${res.accessToken}")

                            withContext(Dispatchers.Main) {
                                navController.navigate(Routes.Login.route)
                            }
                        } catch (e: Exception) {
                            Log.e("signup", "회원가입 실패: ${e.message}")
                        }
                    }
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.Timer.route) { TimerNavGraph(viewModel=tomotoViewModel) }
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