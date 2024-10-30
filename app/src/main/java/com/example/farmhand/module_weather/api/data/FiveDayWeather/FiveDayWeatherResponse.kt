package com.example.farmhand.module_weather.api.data.FiveDayWeather

data class FiveDayWeatherResponse(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<Item0>,
    val message: Int
)