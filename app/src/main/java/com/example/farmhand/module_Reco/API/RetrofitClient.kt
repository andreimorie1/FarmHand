package com.example.farmhand.module_Reco.API

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val USE_MOCK = false  // Set to true for MockOpenAiApiService

    // Configure OkHttpClient with timeouts
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // Connection timeout
        .readTimeout(30, TimeUnit.SECONDS)    // Data reading timeout
        .writeTimeout(30, TimeUnit.SECONDS)   // Data writing timeout
        .build()


    val openAiApiService: OpenAiApiService = if (USE_MOCK) {
        MockOpenAiApiService()
    } else {
        Retrofit.Builder()
            .baseUrl("https://api.openai.com/v1/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenAiApiService::class.java)
    }
}