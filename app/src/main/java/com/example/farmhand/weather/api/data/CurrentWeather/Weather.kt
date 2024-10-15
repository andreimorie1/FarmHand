package com.example.farmhand.weather.api.data.CurrentWeather

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)