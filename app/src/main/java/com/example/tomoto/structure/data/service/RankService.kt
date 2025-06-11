package com.example.tomoto.structure.data.service

import com.example.tomoto.structure.data.dto.response.AllUserInfoRes
import retrofit2.http.GET

interface RankService {

    @GET("/ranking")
    suspend fun getAllUserRanking(): List<AllUserInfoRes>
}