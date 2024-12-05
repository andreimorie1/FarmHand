package com.example.farmhand.module_user.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST


interface UserApiService {

    @POST("register/")
    suspend fun registerFarmer(@Body request: RegisterRequest): ApiResponse

    @POST("login/")
    suspend fun loginFarmer(@Body request: LoginRequest): ApiResponse

    @POST("update/")
    suspend fun updateFarmer(@Body request: UpdateRequest): ApiResponse

    @DELETE("delete/")
    suspend fun deleteFarmer(@Body request: IdRequest): ApiResponse

    @POST("get_farmer/")
    suspend fun getFarmer(@Body request: IdRequest): ApiResponse
}

