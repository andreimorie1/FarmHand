package com.example.farmhand.module_health.api

import com.example.farmhand.module_Reco.API.RetrofitClient
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val USE_MOCK = false

    // Configure OkHttpClient with timeouts
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // Connection timeout
        .readTimeout(30, TimeUnit.SECONDS)    // Data reading timeout
        .writeTimeout(30, TimeUnit.SECONDS)   // Data writing timeout
        .build()

    val kindwiseApiService: KindwiseApiService = if (USE_MOCK) {
        MockKindwiseApiService()
    } else {
        Retrofit.Builder()
            .baseUrl("https://crop.kindwise.com/api/v1/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KindwiseApiService::class.java)
    }
}