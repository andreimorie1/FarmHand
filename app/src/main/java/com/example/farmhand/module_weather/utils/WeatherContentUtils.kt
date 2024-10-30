package com.example.farmhand.module_weather.utils


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.farmhand.R
import com.example.farmhand.ui.theme.Typography
import com.example.farmhand.module_weather.api.data.CurrentWeather.CurrentWeatherResponse
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


// header: String, dataval: String, message: String, icon: Int

/*
Helper functions for processing and formatting module_weather data.
1. InfoCardo
2. Weather Details
3. Wind Direction
4. Season
5. Date Format
6. Weather Icon
 */

@Composable
fun InfoCard(icon: Int, header: String, message: String, dataval: String, modifier: Modifier) {

    // Icon
    Row(
        modifier = modifier
            .fillMaxWidth()  // Ensures equal width from the parent `Row`
            .padding(8.dp),  // Padding around the card contents for consistent layout
        verticalAlignment = Alignment.CenterVertically  // Aligns items vertically in the middle
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(25.dp))
                .background(Color.LightGray)
                .padding(5.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "Sunny Icon",
                modifier = Modifier.size(35.dp)
            )
        }
        Spacer(Modifier.width(5.dp))
        Column {
            Text(header, style = Typography.titleMedium)
            Text(message, style = Typography.bodySmall)
            Text(
                "$dataval${when (header) {
                    "Feels Like" -> "°C"
                    "Humidity" -> "%"
                    "Wind" -> "km/h"
                    "Visibility" -> "km"
                    "Rain" -> "mm"
                    "Cloud" -> "%"
                    "Sea Level", "Air Pressure" -> "hPa"
                    else -> ""
                }}",
                style = Typography.bodySmall
            )
        }
    }
}


// Data class for storing both numeric and simplified module_weather details
data class WeatherDetails(
    val feelsLikeMessage: String,
    val visibilityMessage: String,
    val rainMessage: String,
    val cloudMessage: String,
    val windMessage: String,
    val humidityMessage: String,
    val seaLevelPressureMessage: String,
    val airPressureMessage: String
)

fun getCurrentWeatherDetails(
    currentWeatherData: CurrentWeatherResponse?,
): WeatherDetails {
    val currentWeather = currentWeatherData

    // Feels Like description
    val feelsLikeTemp = currentWeather?.main?.feels_like ?: 0.0
    val feelsLikeMessage = when {
        feelsLikeTemp <= 15.0 -> "Cool"
        feelsLikeTemp <= 25.0 -> "Comfortable"
        else -> "Warm"
    }

    // Visibility description
    val visibility = currentWeather?.visibility ?: 0
    val visibilityMessage = when {
        visibility >= 10000 -> "Good"
        visibility >= 5000 -> "Moderate"
        else -> "Poor"
    }

    // Rain description
    val rainAmount = currentWeather?.rain?.`1h` ?: 0.0
    val rainMessage = when {
        rainAmount == 0.0 -> "No rain"
        rainAmount <= 2.5 -> "Light"
        rainAmount <= 7.6 -> "Moderate"
        else -> "Heavy"
    }

    // Cloud description
    val cloudCoverage = currentWeather?.clouds?.all ?: 0
    val cloudMessage = when {
        cloudCoverage <= 20 -> "Clear"
        cloudCoverage <= 50 -> "Partly Cloudy"
        cloudCoverage <= 84 -> "Mostly Cloudy"
        else -> "Overcast"
    }

    // Wind description
    val windSpeed = (currentWeather?.wind?.speed ?: 0.0) * 3.6 // Convert from m/s to km/h
    val windMessage = when {
        windSpeed <= 5.5 -> "Light"
        windSpeed <= 11.2 -> "Gentle"
        windSpeed <= 20.0 -> "Moderate"
        else -> "Strong"
    }

    // Humidity description
    val humidity = currentWeather?.main?.humidity ?: 0
    val humidityMessage = when {
        humidity <= 40 -> "Low"
        humidity <= 70 -> "Comfortable"
        else -> "High"
    }

    // Sea Level Pressure description
    val seaLevelPressure = currentWeather?.main?.sea_level ?: 0
    val seaLevelPressureMessage = when {
        seaLevelPressure <= 1000 -> "Low"
        seaLevelPressure <= 1020 -> "Normal"
        else -> "High"
    }

    // Air Pressure description
    val airPressure = currentWeather?.main?.pressure ?: 0
    val airPressureMessage = when {
        airPressure <= 1000 -> "Low"
        airPressure <= 1020 -> "Normal"
        else -> "High"
    }

    return WeatherDetails(
        feelsLikeMessage = feelsLikeMessage,
        visibilityMessage = visibilityMessage,
        rainMessage = rainMessage,
        cloudMessage = cloudMessage,
        windMessage = windMessage,
        humidityMessage = humidityMessage,
        seaLevelPressureMessage = seaLevelPressureMessage,
        airPressureMessage = airPressureMessage
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

// Get current season based on the timestamp
fun getSeason(dt: Long): String {
    val month = java.util.Calendar.getInstance().apply { timeInMillis = dt * 1000 }
        .get(java.util.Calendar.MONTH) + 1
    return when (month) {
        in 5..11 -> "Rainy Season"  // May to November
        else -> "Dry/Sunny Season"    // December to April
    }
}

// date format
fun formatDate(dt: Long): String {
    // Create a Date object from the Unix timestamp (dt is in seconds)
    val date = Date(dt * 1000)  // Convert seconds to milliseconds

    // Define the desired date format (e.g., Monday, October 23)
    val dateFormat = SimpleDateFormat("EEEE, MMMM dd", Locale.ENGLISH)

    // Set the time zone to Asia/Manila (Philippines time zone)
    dateFormat.timeZone = TimeZone.getTimeZone("Asia/Manila")

    // Return the formatted date as a string
    return dateFormat.format(date)
}

//   Weather Icon
@Composable
fun WeatherIcon(weather: String?) {
    if (weather != null) {
        when {
            weather.contains("clear", ignoreCase = true) -> {
                Icon(
                    painter = painterResource(id = R.drawable.weather_clear),  // Use the WbSunny icon for sunny module_weather
                    contentDescription = "Sunny Icon",
                    modifier = Modifier.size(48.dp)
                )
            }

            weather.contains("rain", ignoreCase = true) -> {
                Icon(
                    painter = painterResource(id = R.drawable.weather_rainy),  // Use the WbCloudy icon for rainy module_weather
                    contentDescription = "Rainy Icon",
                    modifier = Modifier.size(48.dp)
                )
            }

            weather.contains("thunderstorm", ignoreCase = true) -> {
                Icon(
                    painter = painterResource(id = R.drawable.weather_thunderstorm),  // Use the WbCloudy icon for rainy module_weather
                    contentDescription = "Rainy Icon",
                    modifier = Modifier.size(48.dp)
                )
            }

            weather.contains("clouds", ignoreCase = true) -> {
                Icon(
                    painter = painterResource(id = R.drawable.weather_clouds),  // Use the WbCloudy icon for rainy module_weather
                    contentDescription = "Rainy Icon",
                    modifier = Modifier.size(48.dp)
                )
            }

            weather.contains("drizzle", ignoreCase = true) -> {
                Icon(
                    painter = painterResource(id = R.drawable.weather_drizzle),  // Use the WbCloudy icon for rainy module_weather
                    contentDescription = "Rainy Icon",
                    modifier = Modifier.size(48.dp)
                )
            }

            weather.contains("thunderstorm", ignoreCase = true) -> {
                Icon(
                    painter = painterResource(id = R.drawable.weather_thunderstorm),  // Use the WbCloudy icon for rainy module_weather
                    contentDescription = "Rainy Icon",
                    modifier = Modifier.size(48.dp)
                )
            }

            weather.contains("mist", ignoreCase = true) ||
                    weather.contains("mist", ignoreCase = true) ||
                    weather.contains("haze", ignoreCase = true) ||
                    weather.contains("fog", ignoreCase = true) ||
                    weather.contains("dust", ignoreCase = true) -> {
                Icon(
                    painter = painterResource(id = R.drawable.weather_mist),  // Use the WbCloudy icon for rainy module_weather
                    contentDescription = "Rainy Icon",
                    modifier = Modifier.size(48.dp)
                )
            }

        }
    }


}
