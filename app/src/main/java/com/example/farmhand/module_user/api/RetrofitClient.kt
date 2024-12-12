package com.example.farmhand.module_user.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val retrofit: Retrofit = Retrofit.Builder()
        //"http://10.0.2.2:8000/api/user/" Emulator
        //"http://[ip address]:8000/api/user/" for Actual Phone insert device ip address
        .baseUrl("http://192.168.0.31:8000/api/user/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val userApiService: UserApiService = retrofit.create(UserApiService::class.java)
}

