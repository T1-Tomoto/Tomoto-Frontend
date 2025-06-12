package com.example.tomoto.structure.data.service

import com.example.tomoto.structure.data.dto.response.AllTodoRes
import com.example.tomoto.structure.data.dto.response.UserInfoRes
import retrofit2.http.GET

interface TodoService {

    @GET("/todos")
    suspend fun getAllTodo(): List<AllTodoRes>
}