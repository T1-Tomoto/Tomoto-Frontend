package com.example.tomoto.structure.auth.token

object TokenManager {
    private var token: String? = null

    fun setAccessToken(token: String) {
        this.token = token
    }

    fun getAccessToken(): String? {
        return token
    }
}