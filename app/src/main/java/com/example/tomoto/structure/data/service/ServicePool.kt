package com.example.tomoto.structure.data.service

import com.example.tomoto.structure.data.ApiFactory
import com.example.tomoto.structure.data.dto.response.AllTodoRes

object ServicePool {
    val userService: UserService = ApiFactory.create<UserService>()
    val rankService: RankService = ApiFactory.create<RankService>()
    val pomoService: PomoService = ApiFactory.create<PomoService>()
    val todoService: TodoService = ApiFactory.create<TodoService>()
    val musicService: MusicService = ApiFactory.create<MusicService>()
}