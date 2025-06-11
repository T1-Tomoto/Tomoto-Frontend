package com.example.tomoto.structure.data.service

import com.example.tomoto.structure.data.dto.response.AllUserRankRes
import com.example.tomoto.structure.data.dto.response.FriendsRankRes
import retrofit2.http.GET

interface RankService {

    @GET("/ranking")
    suspend fun getAllUserRanking(): List<AllUserRankRes>

    @GET("/ranking/friends")
    suspend fun getFriendsRanking(): List<FriendsRankRes>

}