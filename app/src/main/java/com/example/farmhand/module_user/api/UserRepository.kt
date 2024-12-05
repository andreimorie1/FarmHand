package com.example.farmhand.module_user.api

import android.util.Log
import javax.inject.Inject
import com.google.gson.Gson


class UserRepository @Inject constructor() {

    suspend fun registerFarmer(request: RegisterRequest): Result<ApiResponse> {
        return try {

            val jsonRequest = Gson().toJson(request)
            Log.d("RequestData", "RegisterRequest JSON: $jsonRequest")

            val response = RetrofitClient.userApiService.registerFarmer(request)
            Result.success(response)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun loginFarmer(request: LoginRequest): Result<ApiResponse> {
        return try {
            val response = RetrofitClient.userApiService.loginFarmer(request)
            Result.success(response)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun updateFarmer(request: UpdateRequest): Result<ApiResponse> {
        return try {
            val response = RetrofitClient.userApiService.updateFarmer(request)
            Result.success(response)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun deleteFarmer(request: IdRequest): Result<ApiResponse> {
        return try {
            val response = RetrofitClient.userApiService.deleteFarmer(request)
            Result.success(response)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun getUserById(farmerId: IdRequest): Result<ApiResponse> {
        return try {
            val response = RetrofitClient.userApiService.getFarmer(farmerId)
            Result.success(response)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
