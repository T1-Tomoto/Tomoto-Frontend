package com.example.tomoto.structure.data.service

import com.example.tomoto.structure.data.dto.request.AddTodoReq
import com.example.tomoto.structure.data.dto.response.AddTodoRes
import com.example.tomoto.structure.data.dto.response.AllTodoRes
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TodoService {

    @GET("/todos")
    suspend fun getAllTodo(): List<AllTodoRes>

    @POST("/todos/add")
    suspend fun addTodo(@Body request: AddTodoReq): AddTodoRes

    @POST("/todos/delete/{todoId}")
    suspend fun deleteTodo(@Path("todoId") todoId: Long): Response<Unit>
}