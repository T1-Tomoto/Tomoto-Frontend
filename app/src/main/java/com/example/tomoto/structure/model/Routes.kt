package com.example.tomoto.structure.model

sealed class Routes (val route : String) {
    data object Login : Routes("Login")
    data object Signup : Routes("Signup")
    data object Timer :Routes("Timer")
    data object TodoList : Routes("TodoList")
    data object Rank : Routes("Rank")
    data object Settings : Routes("Settings")
    data object UserInfo : Routes("UserInfo")
    data object MusicList : Routes("MusicList")
    data object ChallengeList : Routes("ChallengeList")
}