package com.example.farmhand.module_Reco.API

import com.example.farmhand.module_Reco.API.data.OpenAiRequest
import com.example.farmhand.module_Reco.API.data.OpenAiResponse
import javax.inject.Inject

class OpenAiRepository @Inject constructor() {
    private val apiKey = "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"

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
