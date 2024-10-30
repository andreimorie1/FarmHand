package com.example.farmhand.module_weather.api

import com.example.farmhand.module_weather.api.data.CurrentWeather.CurrentWeatherResponse
import com.example.farmhand.module_weather.api.data.FiveDayWeather.FiveDayWeatherResponse
import javax.inject.Inject


class WeatherRepository @Inject constructor() {
    suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double,
        apiKey: String,
        units: String = "metric",
        language: String = "en"
    ): Result<CurrentWeatherResponse> {
        return try {
            val response = RetrofitClient.weatherApiService.getCurrentWeatherByCoordinates(
                latitude = latitude,
                longitude = longitude,
                apiKey = apiKey,
                units = units,
                language = language
            )
            Result.success(response)
        } catch (e: Exception) {
            e.printStackTrace() // Log the exception for better debugging
            Result.failure(e)
        }
    }

    suspend fun getFiveDayForecast(
        latitude: Double,
        longitude: Double,
        apiKey: String,
        units: String = "metric",
        language: String = "en"
    ): Result<FiveDayWeatherResponse> {
        return try {
            val response = RetrofitClient.weatherApiService.getFiveDayForecast(
                latitude = latitude,
                longitude = longitude,
                apiKey = apiKey,
                units = units,
                language = language
            )
            Result.success(response)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}