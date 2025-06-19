package com.example.tomoto.structure.data.service

import com.example.tomoto.structure.data.dto.request.AddTodoReq
import com.example.tomoto.structure.data.dto.request.UpdateMusicReq
import com.example.tomoto.structure.data.dto.response.MusicListRes
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface MusicService {
    @GET("/musics")
    suspend fun getMusicList():List<MusicListRes>

    @POST("/musics")
    suspend fun addMusic(@Query("url") url: String)

    @DELETE("/musics")
    suspend fun deleteMusic(@Query("url") url: String)

    @PATCH("/musics")
    suspend fun updatdMusic(@Body request: UpdateMusicReq)

}