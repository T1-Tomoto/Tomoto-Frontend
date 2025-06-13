package com.example.tomoto.structure.data.service

import com.example.tomoto.structure.data.dto.response.DailyPomoCountDto
import retrofit2.http.GET

interface PomoService {

    @GET("/pomos")
    suspend fun getTodayPomo():Int

    @GET("/pomos/history")
    suspend fun getAllPomoHistory(): List<DailyPomoCountDto>
}