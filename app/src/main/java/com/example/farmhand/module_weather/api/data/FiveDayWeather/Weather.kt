package com.example.farmhand.module_weather.api.data.FiveDayWeather

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)