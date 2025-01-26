package com.example.farmhand.module_Reco.API

import com.example.farmhand.module_Reco.API.data.OpenAiRequest
import com.example.farmhand.module_Reco.API.data.OpenAiResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenAiApiService {

    @POST("chat/completions")
    suspend fun createCompletion(
        @Header("Authorization") authorization: String, 
        @Body request: OpenAiRequest
    ): OpenAiResponse
}
