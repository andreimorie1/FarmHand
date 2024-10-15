package com.example.farmhand.weather.api.data.FiveDayWeather

data class FiveDayWeatherResponse(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<Item0>,
    val message: Int
)