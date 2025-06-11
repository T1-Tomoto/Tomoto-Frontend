package com.example.tomoto.structure.data.service

import retrofit2.http.GET

interface PomoService {

    @GET("/pomos")
    suspend fun getTodayPomo():Int
}