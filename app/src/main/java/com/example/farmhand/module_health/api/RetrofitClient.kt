package com.example.farmhand.module_health.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    object KindwiseRetrofitClient {
        private const val BASE_URL = "https://crop.kindwise.com/api/v1/"

        private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        //val kindwiseApiService: KindwiseApiService = retrofit.create(KindwiseApiService::class.java)
    }
}

