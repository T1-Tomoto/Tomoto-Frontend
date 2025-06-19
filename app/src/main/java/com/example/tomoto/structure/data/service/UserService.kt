package com.example.tomoto.structure.data.service

import com.example.tomoto.structure.data.dto.request.LevelUpdateReq
import com.example.tomoto.structure.data.dto.request.UserLoginReq
import com.example.tomoto.structure.data.dto.request.UserRegisterReq
import com.example.tomoto.structure.data.dto.response.UserInfoRes
import com.example.tomoto.structure.data.dto.response.UserTokenRes
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface UserService {
    @POST("users/signup")
    suspend fun signup(@Body req: UserRegisterReq): UserTokenRes

    @POST("users/login")
    suspend fun login(@Body req: UserLoginReq): UserTokenRes

    @GET("users/info")
    suspend fun info(): UserInfoRes

    @PATCH("users/level")
    suspend fun levelUpdate(@Body req: LevelUpdateReq) : Response<Unit>

    @PATCH("users/challenges")
    suspend fun challengeUpdate(@Body req: List<Boolean>) : Response<Unit>

    @PATCH("users/info/bio")
    suspend fun updateUserBio(@Query("newBio") newBio: String)

}