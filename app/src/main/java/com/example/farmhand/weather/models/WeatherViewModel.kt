package com.example.farmhand.weather.models

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmhand.weather.api.WeatherRepository
import com.example.farmhand.weather.api.data.CurrentWeather.CurrentWeatherResponse
import com.example.farmhand.weather.api.data.FiveDayWeather.FiveDayWeatherResponse
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject


@HiltViewModel
class WeatherViewModel @Inject constructor(private val repository: WeatherRepository) :
    ViewModel() {

    var currentWeatherData: CurrentWeatherResponse? by mutableStateOf(null)
    var fiveDayForecastData: FiveDayWeatherResponse? by mutableStateOf(null)
    var errorMessage: String? by mutableStateOf(null)
    private val _isFetchingData = MutableStateFlow(false)
    val isFetchingData: StateFlow<Boolean> = _isFetchingData


    // latitude and longtitude getter setter
    private val _latitude = MutableStateFlow<Double?>(null)
    private val _longitude = MutableStateFlow<Double?>(null)

    val latitude: StateFlow<Double?> = _latitude.asStateFlow()
    val longitude: StateFlow<Double?> = _longitude.asStateFlow()

    fun updateLocation(latitude: Double, longitude: Double) {
        _latitude.value = latitude
        _longitude.value = longitude
    }

    fun refreshWeatherData(apiKey: String) {
        // Set fetching state to true before starting the fetch
        _isFetchingData.value = true
        // Fetch data
        viewModelScope.launch {
            try {
                // Fetch current weather and forecast
                fetchCurrentWeatherByCoordinates(
                    latitude.value ?: 0.0,
                    longitude.value ?: 0.0,
                    apiKey
                )
                fetchFiveDayForecastByCoordinates(
                    latitude.value ?: 0.0,
                    longitude.value ?: 0.0,
                    apiKey
                )
            } catch (e: Exception) {
                Log.d("WeatherViewModel", "Refresh weather for lat: $latitude, lon: $longitude")

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


    // get CurrentWeather Reponse
    fun fetchCurrentWeatherByCoordinates(latitude: Double, longitude: Double, apiKey: String) {
        Log.d("WeatherViewModel", "Fetching weather for lat: $latitude, lon: $longitude")

        _isFetchingData.value = true // Set fetching status to true before starting the API call

        viewModelScope.launch {
            val result = repository.getCurrentWeather(latitude, longitude, apiKey)
            result.onSuccess {
                Log.d("WeatherViewModel", "Weather data received: $it")
                currentWeatherData = it
                errorMessage = null
            }.onFailure {
                Log.e("WeatherViewModel", "Error fetching weather: ${it}")
                handleFetchError(it)
            }.also {
                delay(1000)
                _isFetchingData.value =
                    false // Set fetching status to false after the API call completes
            }
        }
    }

    //  get Five Day Forecast Response
    fun fetchFiveDayForecastByCoordinates(latitude: Double, longitude: Double, apiKey: String) {
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
                errorMessage = "No internet connection. Please check your network. lol"
            }

            is SocketTimeoutException -> {
                Log.e("WeatherViewModel", "Connection timeout: ${throwable.message}")
                errorMessage = "The request timed out. Please try again. lol"
            }

            is SecurityException -> {
                Log.e("WeatherViewModel", "Location permission denied: ${throwable.message}")
                errorMessage = "Location access is denied. Please enable location permissions. lol"
            }

            else -> {
                Log.e("WeatherViewModel", "Error fetching weather: ${throwable.message}")
                errorMessage = "Failed to fetch weather data. Please try again later. lol"
            }
        }
    }





    // "dt" format to "day, month | hour:minute AM/PM"
    fun convertUnixTimestampToCustomFormat(unixTime: Long): String {
        val date = Date(unixTime * 1000L)
        // format
        val sdf = SimpleDateFormat("dd, MMMM | h:mm a", Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault() // Set to device's default time zone
        return sdf.format(date) // Return formatted date
    }

    //  "dt_txt" format to "day, month | hour:minute AM/PM"
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDateTime(dtTxt: String): String {
        // Parse dt_txt to LocalDateTime
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dateTime = LocalDateTime.parse(dtTxt, inputFormatter)

        // Format it to the desired output format
        val outputFormatter = DateTimeFormatter.ofPattern("d, MMMM | h:mm a", Locale.getDefault())
        return dateTime.format(outputFormatter)
    }

    // Data class for storing both numeric and simplified weather details
    data class WeatherDetails(
        val rainMessage: String,
        val windMessage: String,
        val visibilityMessage: String,
        val cloudMessage: String,
        val rainAmount: Double,
        val windSpeed: Double,
        val visibility: Int,
        val cloudCover: Int,
        val pressure: Int,
        val seaLevel: Int
    )

    // Info Process
    fun getCurrentWeatherDetails(): WeatherDetails {
        val currentWeather = currentWeatherData ?: return WeatherDetails(
            rainMessage = "No rain data",
            windMessage = "No wind data",
            visibilityMessage = "No visibility data",
            cloudMessage = "No cloud data",
            rainAmount = 0.0,
            windSpeed = 0.0,
            visibility = 0,
            cloudCover = 0,
            pressure = 0,
            seaLevel = 0
        )

        val rainAmount = currentWeather.rain?.`1h` ?: 0.0  // null safe incase of missing value
        val rainMessage = when {
            rainAmount <= 2.5 -> "Light rain"
            rainAmount <= 7.6 -> "Moderate rain"
            else -> "Heavy rain"
        }

        val windSpeed = currentWeather.wind.speed * 3.6 // Convert from m/s to km/h
        val windMessage = when {
            windSpeed <= 5.5 -> "Light breeze"
            windSpeed <= 11.2 -> "Gentle breeze"
            windSpeed <= 20.0 -> "Moderate breeze"
            else -> "Strong breeze"
        }

        val visibilityMessage =
            if (currentWeather.visibility >= 10000) "Good visibility" else "Visibility is reduced"
        val cloudMessage = when {
            currentWeather.clouds.all <= 20 -> "Clear skies"
            currentWeather.clouds.all <= 50 -> "Partly cloudy"
            currentWeather.clouds.all <= 84 -> "Mostly cloudy"
            else -> "Overcast"
        }


        return WeatherDetails(
            rainMessage = rainMessage,
            windMessage = windMessage,
            visibilityMessage = visibilityMessage,
            cloudMessage = cloudMessage,
            rainAmount = rainAmount,
            windSpeed = currentWeather.wind.speed,
            visibility = currentWeather.visibility,
            cloudCover = currentWeather.clouds.all,
            pressure = currentWeather.main.pressure,
            seaLevel = currentWeather.main.sea_level
        )
    }


    // Helper function to convert wind direction degrees into a cardinal direction
    private fun getWindDirection(degrees: Int): String {
        return when ((degrees % 360) / 45) {
            0 -> "north"
            1 -> "northeast"
            2 -> "east"
            3 -> "southeast"
            4 -> "south"
            5 -> "southwest"
            6 -> "west"
            7 -> "northwest"
            else -> "unknown"
        }
    }
}