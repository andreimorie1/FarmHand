package com.example.farmhand.module_health.api

import com.example.farmhand.module_health.api.data.post_identification.actual.ActualIdentificationResponse
import com.example.farmhand.module_health.api.data.post_identification.actual.CreateidentificationRequest
import javax.inject.Inject

class KindwiseRepository @Inject constructor() {
    suspend fun createIdentification(request: CreateidentificationRequest): Result<ActualIdentificationResponse> {
        return try {
            val apiKey = "d3eEobXnaO6OZSJfOLMO7XfMgc2PDGN7kzXdTfk7qAgg07IZdv"
            val response = RetrofitClient.kindwiseApiService.createIdentification(apiKey, request)
            Result.success(response)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun getIdentification(accessToken: String): Result<ActualIdentificationResponse> {
        return try {
            val response = RetrofitClient.kindwiseApiService.getIdentification(accessToken)
            Result.success(response)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}