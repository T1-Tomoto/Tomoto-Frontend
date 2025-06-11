package com.example.tomoto.structure.auth.token

object TokenManager {
    private var accessToken: String? = null

    fun setAccessToken(token: String) {
        accessToken = token
    }

    fun getAccessToken(): String? = accessToken
}