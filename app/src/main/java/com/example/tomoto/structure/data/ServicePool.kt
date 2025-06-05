package com.example.tomoto.structure.data

import com.example.tomoto.structure.data.service.UserService

object ServicePool {
    val userService = ApiFactory.create<UserService>()
}