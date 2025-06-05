package com.example.tomoto.structure.data.service

import com.example.tomoto.structure.data.dto.request.UserRegisterReq
import com.example.tomoto.structure.data.dto.response.UserTokenRes
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("users/signup")
    suspend fun signup(@Body req: UserRegisterReq): UserTokenRes

//    @POST("users/login")
//    suspend fun login(@Body req: UserLoginReq): UserTokenRes
}