package com.example.farmhand.module_weather.api.data

import com.example.farmhand.module_weather.api.data.CurrentWeather.CurrentWeatherResponse
import com.example.farmhand.module_weather.api.data.FiveDayWeather.FiveDayWeatherResponse

data class WeatherScreenState(
    val currentWeather: CurrentWeatherResponse? = null,
    val fiveDayWeather: FiveDayWeatherResponse? = null,
    val isRefreshing: Boolean = false
)
