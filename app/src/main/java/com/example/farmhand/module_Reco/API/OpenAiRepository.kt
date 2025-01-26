package com.example.farmhand.module_Reco.API

import com.example.farmhand.module_Reco.API.data.OpenAiRequest
import com.example.farmhand.module_Reco.API.data.OpenAiResponse
import javax.inject.Inject

class OpenAiRepository @Inject constructor() {
    private val apiKey = "API_KEY"

    suspend fun createCompletion(request: OpenAiRequest): Result<OpenAiResponse> {
        return try {
            val response = RetrofitClient.openAiApiService.createCompletion("Bearer $apiKey", request)
            Result.success(response)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
