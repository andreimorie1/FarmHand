package com.example.farmhand.weather.api.data

import com.example.farmhand.weather.api.data.CurrentWeather.CurrentWeatherResponse
import com.example.farmhand.weather.api.data.FiveDayWeather.FiveDayWeatherResponse

data class WeatherScreenState(
    val currentWeather: CurrentWeatherResponse? = null,
    val fiveDayWeather: FiveDayWeatherResponse? = null,
    val isRefreshing: Boolean = false
)
