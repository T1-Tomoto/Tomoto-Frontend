package com.example.tomoto.structure.data.service

import com.example.tomoto.structure.data.ApiFactory

object ServicePool {
    val userService: UserService = ApiFactory.create<UserService>()
    val rankService: RankService = ApiFactory.create<RankService>()
    val pomoService: PomoService = ApiFactory.create<PomoService>()
}