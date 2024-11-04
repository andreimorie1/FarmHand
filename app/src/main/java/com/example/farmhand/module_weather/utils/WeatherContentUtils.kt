package com.example.farmhand.module_weather.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.farmhand.R
import com.example.farmhand.module_weather.api.data.CurrentWeather.CurrentWeatherResponse
import com.example.farmhand.module_weather.api.data.ThirtyDayWeather.Item0
import com.example.farmhand.module_weather.api.data.ThirtyDayWeather.ThirtyDayForecastResponse
import com.example.farmhand.ui.theme.Typography
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt


// header: String, dataval: String, message: String, icon: Int

/*
Helper functions for processing and formatting module_weather data.
1. InfoCard
2. Weather Details
3. Wind Direction
4. Season
5. Date Format
6. date format shorter
7. Weather Icon
8. 30 day forecast
9. Header Composable
10. Current Details Composable
 */

@Composable
fun InfoCard(icon: Int?, header: String, message: String, dataval: String) {

    // Icon
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            tonalElevation = 10.dp,
            shadowElevation = 5.dp,
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier
                .size(50.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceDim),
                contentAlignment = Alignment.Center
            ) {
                when (icon) {
                    null -> {
                        // Empty Icon
                    }

                    else -> {
                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = "Sunny Icon",
                            modifier = Modifier.size(35.dp)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.width(5.dp))
        Column {
            Text(
                header,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "$dataval${
                    when (header) {
                        "Feels Like" -> "°C"
                        "Humidity" -> "%"
                        "Wind" -> "km/h"
                        "Visibility" -> "km"
                        "Rain" -> "mm"
                        "Cloud" -> "%"
                        "Sea Level", "Air Pressure" -> "hPa"
                        else -> ""
                    }
                }",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun AlertInfoCard(icon: Int, header: String, message: String, dataval: String, modifier: Modifier) {
    Row(Modifier.padding(vertical = 3.dp)) {
        Box(
            modifier = Modifier
                .size(35.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(5.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "Sunny Icon"
            )
        }
        Spacer(Modifier.width(5.dp))
        Column {
            Text(header, style = Typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(
                "$dataval${
                    when (header) {
                        "Feels Like" -> "°C"
                        "Humidity" -> "%"
                        "Wind" -> "km/h"
                        "Visibility" -> "km"
                        "Rain" -> "mm"
                        "Cloud" -> "%"
                        "Sea Level", "Air Pressure" -> "hPa"
                        else -> ""
                    }
                } | $message",
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

data class ThirtyDayWeatherDetails(
    val feelsLikeMessageMorn: String,
    val feelsLikeMessageDay: String,
    val feelsLikeMessageEve: String,
    val feelsLikeMessageNight: String,
    val rainMessage: String,
    val cloudMessage: String,
    val windMessage: String,
    val humidityMessage: String,
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

fun getThirtyWeatherDetails(
    forecast: Item0,
): ThirtyDayWeatherDetails {

    // Feels Like description
    val feelsLikeTempMorn = forecast.feels_like.morn
    val feelsLikeMessageMorn = when {
        feelsLikeTempMorn <= 10.0 -> "Very Cold"  // Below 10°C
        feelsLikeTempMorn in 11.0..15.0 -> "Cool"
        feelsLikeTempMorn in 16.0..20.0 -> "Mild"
        feelsLikeTempMorn in 21.0..25.0 -> "Comfortable"
        feelsLikeTempMorn in 26.0..30.0 -> "Warm"
        feelsLikeTempMorn in 31.0..35.0 -> "Hot"
        feelsLikeTempMorn in 36.0..40.0 -> "Very Hot"
        else -> "Extremely Hot"  // Above 40°C
    }
    val feelsLikeTempDay = forecast.feels_like.day
    val feelsLikeMessageDay = when {
        feelsLikeTempDay <= 10.0 -> "Very Cold"
        feelsLikeTempDay in 11.0..15.0 -> "Cool"
        feelsLikeTempDay in 16.0..20.0 -> "Mild"
        feelsLikeTempDay in 21.0..25.0 -> "Comfortable"
        feelsLikeTempDay in 26.0..30.0 -> "Warm"
        feelsLikeTempDay in 31.0..35.0 -> "Hot"
        feelsLikeTempDay in 36.0..40.0 -> "Very Hot"
        else -> "Extremely Hot"  // Above 40°C
    }
    val feelsLikeTempEve = forecast.feels_like.eve
    val feelsLikeMessageEve = when {
        feelsLikeTempEve <= 10.0 -> "Very Cold"
        feelsLikeTempEve in 11.0..15.0 -> "Cool"
        feelsLikeTempEve in 16.0..20.0 -> "Mild"
        feelsLikeTempEve in 21.0..25.0 -> "Comfortable"
        feelsLikeTempEve in 26.0..30.0 -> "Warm"
        feelsLikeTempEve in 31.0..35.0 -> "Hot"
        feelsLikeTempEve in 36.0..40.0 -> "Very Hot"
        else -> "Extremely Hot"  // Above 40°C
    }
    val feelsLikeTempNight = forecast.feels_like.night
    val feelsLikeMessageNight = when {
        feelsLikeTempNight <= 10.0 -> "Very Cold"
        feelsLikeTempNight in 11.0..15.0 -> "Cool"
        feelsLikeTempNight in 16.0..20.0 -> "Mild"
        feelsLikeTempNight in 21.0..25.0 -> "Comfortable"
        feelsLikeTempNight in 26.0..30.0 -> "Warm"
        feelsLikeTempNight in 31.0..35.0 -> "Hot"
        feelsLikeTempNight in 36.0..40.0 -> "Very Hot"
        else -> "Extremely Hot"  // Above 40°C
    }

    // Rain description
    val rainAmount = forecast.rain
    val rainMessage = when {
        rainAmount == 0.0 -> "No rain"
        rainAmount <= 2.5 -> "Light"
        rainAmount <= 7.6 -> "Moderate"
        else -> "Heavy"
    }

    // Cloud description
    val cloudCoverage = forecast.clouds
    val cloudMessage = when {
        cloudCoverage <= 20 -> "Clear"
        cloudCoverage <= 50 -> "Partly Cloudy"
        cloudCoverage <= 84 -> "Mostly Cloudy"
        else -> "Overcast"
    }

    // Wind description
    val windSpeed = (forecast.speed) * 3.6 // Convert from m/s to km/h
    val windMessage = when {
        windSpeed <= 5.5 -> "Light"
        windSpeed <= 11.2 -> "Gentle"
        windSpeed <= 20.0 -> "Moderate"
        else -> "Strong"
    }

    // Humidity description
    val humidity = forecast.humidity
    val humidityMessage = when {
        humidity <= 40 -> "Low"
        humidity <= 70 -> "Comfortable"
        else -> "High"
    }

    // Air Pressure description
    val airPressure = forecast.pressure
    val airPressureMessage = when {
        airPressure <= 1000 -> "Low"
        airPressure <= 1020 -> "Normal"
        else -> "High"
    }

    return ThirtyDayWeatherDetails(
        feelsLikeMessageMorn = feelsLikeMessageMorn,
        feelsLikeMessageDay = feelsLikeMessageDay,
        feelsLikeMessageEve = feelsLikeMessageEve,
        feelsLikeMessageNight = feelsLikeMessageNight,
        rainMessage = rainMessage,
        cloudMessage = cloudMessage,
        windMessage = windMessage,
        humidityMessage = humidityMessage,
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

// date format shorter
fun formatDateShort(dt: Long): String {
    val date = Date(dt * 1000)

    val dateFormat = SimpleDateFormat("EEE, MMM dd", Locale.ENGLISH)

    dateFormat.timeZone = TimeZone.getTimeZone("Asia/Manila")

    return dateFormat.format(date)
}

// date format 12 hour AM/PM
fun formatUnixTimeTo12Hour(unixTime: Long): String {
    val date = Date(unixTime * 1000) // Convert seconds to milliseconds

    val timeFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)

    timeFormat.timeZone = TimeZone.getTimeZone("Asia/Manila")

    return timeFormat.format(date)
}

//   Weather Icon
@Composable
fun WeatherIcon(weather: String?, modifier: Modifier) {
    if (weather != null) {
        when {
            weather.contains("clear", ignoreCase = true) -> {
                Icon(
                    painter = painterResource(id = R.drawable.weather_clear),  // Use the WbSunny icon for sunny module_weather
                    contentDescription = "Sunny Icon",
                    modifier = modifier
                )
            }

            weather.contains("rain", ignoreCase = true) -> {
                Icon(
                    painter = painterResource(id = R.drawable.weather_rainy),  // Use the WbCloudy icon for rainy module_weather
                    contentDescription = "Rainy Icon",
                    modifier = modifier
                )
            }

            weather.contains("thunderstorm", ignoreCase = true) -> {
                Icon(
                    painter = painterResource(id = R.drawable.weather_thunderstorm),  // Use the WbCloudy icon for rainy module_weather
                    contentDescription = "Rainy Icon",
                    modifier = modifier
                )
            }

            weather.contains("clouds", ignoreCase = true) -> {
                Icon(
                    painter = painterResource(id = R.drawable.weather_clouds),  // Use the WbCloudy icon for rainy module_weather
                    contentDescription = "Rainy Icon",
                    modifier = modifier
                )
            }

            weather.contains("drizzle", ignoreCase = true) -> {
                Icon(
                    painter = painterResource(id = R.drawable.weather_drizzle),  // Use the WbCloudy icon for rainy module_weather
                    contentDescription = "Rainy Icon",
                    modifier = modifier
                )
            }

            weather.contains("thunderstorm", ignoreCase = true) -> {
                Icon(
                    painter = painterResource(id = R.drawable.weather_thunderstorm),  // Use the WbCloudy icon for rainy module_weather
                    contentDescription = "Rainy Icon",
                    modifier = modifier
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
                    modifier = modifier
                )
            }

        }
    }
}

// 30 day forecast
@Composable
fun ForecastLazyRow(ThirtyDayForecastResponse: ThirtyDayForecastResponse) {
    LazyRow {
        items(ThirtyDayForecastResponse.list) { forecastItem ->
            ForecastCard(forecastItem)
        }
    }
}

// 30-day daily forecast card
@Composable
fun ForecastCard(forecastItem: Item0) {
    var showDialog by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier
            .padding(8.dp)
            .height(120.dp)
            .width(100.dp),
        shape = MaterialTheme.shapes.large,
        tonalElevation = 10.dp,
        shadowElevation = 8.dp,
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .clickable { showDialog = true },
            content = {
                Column(
                    modifier = Modifier // 30-day daily forecast card Background
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    // Icon
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(2.dp)
                        ) {
                            WeatherIcon(
                                forecastItem.weather.firstOrNull()?.main,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                    // Description
                    Spacer(Modifier.height(5.dp))

                    Text(
                        forecastItem.weather.firstOrNull()?.description ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        formatDateShort(forecastItem.dt.toLong()),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "${forecastItem.temp.min.roundToInt()}°C | ${forecastItem.temp.max.roundToInt()}°C",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        )
    }

    if (showDialog) {
        val WeatherDetails = getThirtyWeatherDetails(forecastItem)

        AlertDialog(
            onDismissRequest = { showDialog = false },
            icon = {
                WeatherIcon(
                    forecastItem.weather.firstOrNull()?.main,
                    modifier = Modifier.size(55.dp)
                )
            },
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Climactic Forecast", style = Typography.bodySmall)
                    Text(
                        text = formatDate(forecastItem.dt.toLong()),
                        style = Typography.titleMedium
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(horizontal = 50.dp, vertical = 3.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "${forecastItem.weather.firstOrNull()?.main}. ${forecastItem.weather.firstOrNull()?.description}.",
                        style = Typography.titleLarge
                    )
                    Text(
                        text = "High will be ${forecastItem.feels_like.day}°C, Low will be ${forecastItem.feels_like.night}°C",
                        style = Typography.bodyMedium
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        // col 1
                        Column {
                            AlertInfoCard(
                                icon = R.drawable.weather_clear,
                                header = "Humidity",
                                message = WeatherDetails.humidityMessage,
                                dataval = forecastItem.humidity.toString(),
                                modifier = Modifier.weight(1f)
                            )
                            AlertInfoCard(
                                icon = R.drawable.weather_clear,
                                header = "Rain",
                                message = WeatherDetails.rainMessage,
                                dataval = forecastItem.rain.toString(),
                                modifier = Modifier.weight(1f)
                            )
                            AlertInfoCard(
                                icon = R.drawable.weather_clear,
                                header = "Cloud",
                                message = WeatherDetails.cloudMessage,
                                dataval = forecastItem.clouds.toString(),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // col 2
                        Column {
                            AlertInfoCard(
                                icon = R.drawable.weather_clear,
                                header = "Feels Like",
                                message = WeatherDetails.humidityMessage,
                                dataval = forecastItem.humidity.toString(),
                                modifier = Modifier.weight(1f)
                            )
                            AlertInfoCard(
                                icon = R.drawable.weather_clear,
                                header = "Wind",
                                message = WeatherDetails.windMessage,
                                dataval = forecastItem.speed.toString(),
                                modifier = Modifier.weight(1f)
                            )
                            AlertInfoCard(
                                icon = R.drawable.weather_clear,
                                header = "Air Press.",
                                message = WeatherDetails.airPressureMessage,
                                dataval = forecastItem.pressure.toString(),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(Modifier.height(5.dp))
                    //table
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize()

                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Text(text = "     ", style = Typography.bodySmall)
                            Text(
                                text = "Morning",
                                style = Typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Afternoon",
                                style = Typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Evening",
                                style = Typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Night",
                                style = Typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Temp",
                                style = Typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                            Text(text = "${forecastItem.temp.morn}°C", style = Typography.bodySmall)
                            Text(text = "${forecastItem.temp.day}°C", style = Typography.bodySmall)
                            Text(text = "${forecastItem.temp.eve}°C", style = Typography.bodySmall)
                            Text(
                                text = "${forecastItem.temp.night}°C",
                                style = Typography.bodySmall
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Feels",
                                style = Typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${forecastItem.feels_like.morn}°C",
                                style = Typography.bodySmall
                            )
                            Text(
                                text = "${forecastItem.feels_like.day}°C",
                                style = Typography.bodySmall
                            )
                            Text(
                                text = "${forecastItem.feels_like.eve}°C",
                                style = Typography.bodySmall
                            )
                            Text(
                                text = "${forecastItem.feels_like.night}°C",
                                style = Typography.bodySmall
                            )
                        }

                        Spacer(Modifier.height(5.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(0.6f)
                                    .padding(end = 3.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = "Morning",
                                    style = Typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Afternoon",
                                    style = Typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Evening",
                                    style = Typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Night",
                                    style = Typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 3.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = WeatherDetails.feelsLikeMessageMorn,
                                    style = Typography.bodySmall
                                )
                                Text(
                                    text = WeatherDetails.feelsLikeMessageDay,
                                    style = Typography.bodySmall
                                )
                                Text(
                                    text = WeatherDetails.feelsLikeMessageEve,
                                    style = Typography.bodySmall
                                )
                                Text(
                                    text = WeatherDetails.feelsLikeMessageNight,
                                    style = Typography.bodySmall
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "Sunrise",
                                    style = Typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    formatUnixTimeTo12Hour(forecastItem.sunrise.toLong()),
                                    style = Typography.bodySmall
                                )
                                Text(
                                    "Sunset",
                                    style = Typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    formatUnixTimeTo12Hour(forecastItem.sunset.toLong()),
                                    style = Typography.bodySmall
                                )
                            }


                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Close")
                }
            },
            tonalElevation = 6.dp,
            properties = DialogProperties(dismissOnClickOutside = true, dismissOnBackPress = true)
        )
    }
}

// Header Composable
@Composable
fun WeatherHeader(currentWeatherData: CurrentWeatherResponse?) {
    Row(
        Modifier.fillMaxSize()
    ) {
        //col 1
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // City name
            Text(
                modifier = Modifier.padding(bottom = 1.dp),
                text = currentWeatherData?.name ?: "",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            // Date
            Text(
                modifier = Modifier.padding(bottom = 2.dp),
                text = formatDate(currentWeatherData?.dt?.toLong() ?: 0L),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            // Main Temperature
            Text(
                text = "${currentWeatherData?.main?.temp?.roundToInt()}°C",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            // Season
            Text(
                text = getSeason(currentWeatherData?.dt?.toLong() ?: 0L),
                style = Typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            // Weather Description
            Text(
                text = "${currentWeatherData?.weather?.firstOrNull()?.description}",
                modifier = Modifier,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        // col 2
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp)
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            WeatherIcon(
                currentWeatherData?.weather?.firstOrNull()?.main,
                modifier = Modifier.size(110.dp)
            )
        }
    }
}

// Current Details Composable
@Composable
fun CurrentWeatherDetails(
    currentWeatherData: CurrentWeatherResponse,
    WeatherDetails: WeatherDetails
) {

    Surface(
        modifier = Modifier
            .wrapContentSize()
            .padding(vertical = 5.dp, horizontal = 20.dp),
        tonalElevation = 10.dp,
        shadowElevation = 10.dp,
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(// Current Details Composable Background
                    color = MaterialTheme.colorScheme.surfaceContainer
                )
                .padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${currentWeatherData.weather.firstOrNull()?.main ?: ""}. ${currentWeatherData.weather.firstOrNull()?.description ?: ""}.",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 5.dp)
            )
            HorizontalDivider(thickness = 1.3.dp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 70.dp, vertical = 3.dp))
            // TEST
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .weight(1f),
                ) {
                    InfoCard(
                        icon = R.drawable.weather_clear,
                        header = "Humidity",
                        message = WeatherDetails.humidityMessage,
                        dataval = currentWeatherData.main.humidity.toString(),
                    )
                    InfoCard(
                        icon = R.drawable.weather_clear,
                        header = "Visibility",
                        message = WeatherDetails.visibilityMessage,
                        dataval = currentWeatherData.visibility.toString(),
                    )
                    InfoCard(
                        icon = R.drawable.weather_clear,
                        header = "Rain",
                        message = WeatherDetails.rainMessage,
                        dataval = currentWeatherData.rain?.`1h`?.toString()
                            ?: "0.0",
                    )
                    InfoCard(
                        icon = R.drawable.weather_clear,
                        header = "Cloud",
                        message = WeatherDetails.cloudMessage,
                        dataval = currentWeatherData.clouds.all.toString(),
                    )
                }
                Column(
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .weight(1f),
                ) {
                    Column {
                        InfoCard(
                            icon = R.drawable.weather_clear,
                            header = "Feels Like",
                            message = WeatherDetails.feelsLikeMessage,
                            dataval = currentWeatherData.main.feels_like.toString(),
                        )
                        InfoCard(
                            icon = R.drawable.weather_clear,
                            header = "Wind",
                            message = WeatherDetails.windMessage,
                            dataval = currentWeatherData.wind.speed.toString(),
                        )
                        InfoCard(
                            icon = R.drawable.weather_clear,
                            header = "Sea Level",
                            message = WeatherDetails.seaLevelPressureMessage,
                            dataval = currentWeatherData.main.sea_level.toString(),
                        )
                        InfoCard(
                            icon = R.drawable.weather_clear,
                            header = "Air Press.",
                            message = WeatherDetails.airPressureMessage,
                            dataval = currentWeatherData.main.pressure.toString(),
                        )
                    }
                }
            }
        }
    }

}