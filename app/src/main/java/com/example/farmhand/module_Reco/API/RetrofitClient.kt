package com.example.farmhand.module_Reco.API

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val USE_MOCK = true  // Set to true for MockOpenAiApiService

    val openAiApiService: OpenAiApiService = if (USE_MOCK) {
        MockOpenAiService.MockOpenAiApiService()
    } else {
        Retrofit.Builder()
            .baseUrl("https://api.openai.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenAiApiService::class.java)
    }
}