package com.example.farmhand.module_health.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val USE_MOCK = true  // Set to true to use MockKindwiseApiService

    val kindwiseApiService: KindwiseApiService = if (USE_MOCK) {
        MockKindwiseApiService()
    } else {
        Retrofit.Builder()
            .baseUrl("https://crop.kindwise.com/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KindwiseApiService::class.java)
    }
}

