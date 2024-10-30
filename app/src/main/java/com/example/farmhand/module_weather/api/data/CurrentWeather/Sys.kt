package com.example.farmhand.module_weather.api.data.CurrentWeather

data class Sys(
    val country: String,
    val id: Int,
    val sunrise: Int,
    val sunset: Int,
    val type: Int
)