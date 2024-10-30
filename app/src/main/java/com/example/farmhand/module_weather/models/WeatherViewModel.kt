package com.example.farmhand.module_weather.models


import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmhand.module_weather.api.WeatherRepository
import com.example.farmhand.module_weather.api.data.CurrentWeather.CurrentWeatherResponse
import com.example.farmhand.module_weather.api.data.FiveDayWeather.FiveDayWeatherResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException


@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val locationManager: LocationManager,
    private val context: Context
) :
    ViewModel() {

    var currentWeatherData: CurrentWeatherResponse? by mutableStateOf(null)
    var fiveDayForecastData: FiveDayWeatherResponse? by mutableStateOf(null)
    var errorMessage: String? by mutableStateOf(null)
    private val _isFetchingData = MutableStateFlow(false)
    val isFetchingData: StateFlow<Boolean> = _isFetchingData

    init {
        _isFetchingData.value = true
        observeFetchingState() // Start observing the isFetchingData state
        _isFetchingData.value = false

    }

    // Function to observe fetching state and manage location updates accordingly
    private fun observeFetchingState() {
        viewModelScope.launch {
            isFetchingData.collect { isFetching ->
                if (isFetching) {
                    locationManager.startLocationUpdates() // Start location updates
                } else {
                    delay(500)
                    locationManager.stopLocationUpdates() // Stop location updates when fetching is done
                }
            }
        }
    }

    fun refreshWeatherData(latitude: Double, longitude: Double, apiKey: String) {
        // Set fetching state to true before starting the fetch
        _isFetchingData.value = true
        // Fetch data
        viewModelScope.launch {
            try {
                observeFetchingState()
                // Fetch current module_weather and forecast
                fetchCurrentWeatherByCoordinates(
                    latitude = latitude,
                    longitude = longitude,
                    apiKey
                )
                fetchFiveDayForecastByCoordinates(
                    latitude = latitude,
                    longitude = longitude,
                    apiKey
                )
            } catch (e: Exception) {
                Log.d("WeatherViewModel", "Refresh module_weather for lat: $latitude, lon: $longitude")

                // Handle any errors
                errorMessage = e.message
            } finally {
                // Reset fetching state after the data has been fetched
                delay(1000)
                _isFetchingData.value =
                    false // Set fetching status to false after the API call completes
            }
        }
    }


    // get CurrentWeather Response
    private fun fetchCurrentWeatherByCoordinates(latitude: Double, longitude: Double, apiKey: String) {
        Log.d("WeatherViewModel", "Fetching module_weather for lat: $latitude, lon: $longitude")

        _isFetchingData.value = true // Set fetching status to true before starting the API call

        viewModelScope.launch {
            val result = repository.getCurrentWeather(latitude, longitude, apiKey)
            result.onSuccess {
                Log.d("WeatherViewModel", "Weather data received: $it")
                currentWeatherData = it
                errorMessage = null
            }.onFailure {
                Log.e("WeatherViewModel", "Error fetching module_weather: $it")
                handleFetchError(it)
            }.also {
                delay(1000)
                _isFetchingData.value =
                    false // Set fetching status to false after the API call completes
            }
        }
    }

    //  get Five Day Forecast Response
    private fun fetchFiveDayForecastByCoordinates(latitude: Double, longitude: Double, apiKey: String) {
        Log.d("WeatherViewModel", "Fetching 5-day forecast for lat: $latitude, lon: $longitude")

        _isFetchingData.value = true // Set fetching status to true before starting the API call

        viewModelScope.launch {
            val result = repository.getFiveDayForecast(latitude, longitude, apiKey)
            result.onSuccess {
                Log.d("WeatherViewModel", "5-day forecast data received: $it")
                fiveDayForecastData = it
                errorMessage = null
            }.onFailure {
                Log.e("WeatherViewModel", "Error fetching 5-day forecast: ${it}")
                handleFetchError(it)
            }.also {
                delay(1000)
                _isFetchingData.value =
                    false // Set fetching status to false after the API call completes
            }
        }
    }

    // Handle specific error scenarios
    private fun handleFetchError(throwable: Throwable) {
        when (throwable) {
            is UnknownHostException -> {
                Log.e("WeatherViewModel", "No internet connection: ${throwable.message}")
                errorMessage = "No internet connection. Please check your network."
            }

            is SocketTimeoutException -> {
                Log.e("WeatherViewModel", "Connection timeout: ${throwable.message}")
                errorMessage = "The request timed out. Please try again."
            }

            is SecurityException -> {
                Log.e("WeatherViewModel", "Location permission denied: ${throwable.message}")
                errorMessage = "Location access is denied. Please enable location permissions."
            }

            else -> {
                Log.e("WeatherViewModel", "Error fetching module_weather: ${throwable.message}")
                errorMessage = "Failed to fetch module_weather data. Please try again later."
            }
        }
    }
}