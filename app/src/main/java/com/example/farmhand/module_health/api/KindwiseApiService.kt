package com.example.farmhand.module_health.api

import com.example.farmhand.module_health.api.data.post_identification.actual.ActualIdentificationResponse
import com.example.farmhand.module_health.api.data.post_identification.actual.CreateidentificationRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface KindwiseApiService {

    @POST("identification")
    suspend fun createIdentification(
        @Header("Api-Key") apiKey: String,
        @Body request: CreateidentificationRequest
    ): ActualIdentificationResponse

    @GET("identification/{access_token}")
    suspend fun getIdentification(
        @Path("access_token") accessToken: String
    ): ActualIdentificationResponse

}