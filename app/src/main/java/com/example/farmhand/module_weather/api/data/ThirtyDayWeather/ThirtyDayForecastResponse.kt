package com.example.farmhand.module_weather.api.data.ThirtyDayWeather

data class ThirtyDayForecastResponse(
    val city: City,
    val cod: String,
    val list: List<Item0>,
    val message: Double
)