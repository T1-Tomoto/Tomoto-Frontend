package com.example.tomoto.structure.data.service

import com.example.tomoto.structure.data.dto.request.AddFriendReq
import com.example.tomoto.structure.data.dto.response.AllUserRankRes
import com.example.tomoto.structure.data.dto.response.FriendsRankRes
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RankService {

    @GET("/ranking")
    suspend fun getAllUserRanking(): List<AllUserRankRes>

    @GET("/ranking/friends")
    suspend fun getFriendsRanking(): List<FriendsRankRes>

    @POST("/ranking/friends")
    suspend fun addFriend(@Body request: AddFriendReq)

}