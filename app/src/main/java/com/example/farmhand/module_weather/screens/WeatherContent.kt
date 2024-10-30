package com.example.farmhand.module_weather.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.farmhand.R
import com.example.farmhand.ui.theme.Typography
import com.example.farmhand.module_weather.api.data.CurrentWeather.CurrentWeatherResponse
import com.example.farmhand.module_weather.api.data.FiveDayWeather.FiveDayWeatherResponse
import com.example.farmhand.module_weather.models.WeatherViewModel
import com.example.farmhand.module_weather.utils.InfoCard
import com.example.farmhand.module_weather.utils.WeatherIcon
import com.example.farmhand.module_weather.utils.formatDate
import com.example.farmhand.module_weather.utils.getCurrentWeatherDetails
import com.example.farmhand.module_weather.utils.getWeatherRecommendation
import kotlin.math.roundToInt


@Composable
fun WeatherContent(
    isFetchingData: Boolean,
    viewModel: WeatherViewModel,
    currentWeatherData: CurrentWeatherResponse?,
    fiveDayForecastData: FiveDayWeatherResponse?,
    errorMessage: String?,
) {
    if (isFetchingData) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "Fetching Weather Data",
                style = Typography.titleMedium,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    } else {
        when {
            currentWeatherData != null && fiveDayForecastData != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .background(
                            if (isSystemInDarkTheme()) {
                                Color(0xFF424242)  // Slightly lighter in dark mode
                            } else {
                                Color(0xFFEBEBEB) // Slightly darker in light mode
                            }
                        ),
                    verticalArrangement = Arrangement.Top,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(12.dp),
                    ) {
                        Column(
                            modifier = Modifier
                                .wrapContentHeight(),
                            horizontalAlignment = Alignment.Start,
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // City name
                                Text(
                                    text = currentWeatherData.name,
                                    style = Typography.headlineMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                // Date
                                Text(
                                    text = formatDate(currentWeatherData.dt.toLong()),
                                    style = Typography.titleSmall,
                                    fontWeight = FontWeight.Normal,
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Main Temperature
                                Text(
                                    text = "${currentWeatherData.main.temp.roundToInt()}°C",
                                    style = Typography.displayMedium,
                                    modifier = Modifier.weight(1f) // Weight is fine here
                                )
                                // Main Icon
                                Box(
                                    modifier = Modifier
                                        .weight(.5f) // Takes up available space equally
                                        .fillMaxWidth(), // Fills the available width from weight
                                    contentAlignment = Alignment.Center // Centers the icon within this Box
                                ) {
                                    WeatherIcon(currentWeatherData.weather.firstOrNull()?.main)
                                }
                            }
                            // Weather Description
                            Text(
                                text = "${currentWeatherData.weather.firstOrNull()?.description}",
                                modifier = Modifier,
                                style = Typography.titleMedium,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                    //Main
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 15.dp, end = 10.dp, top = 15.dp),
                    ) {
                        Text(
                            "Weather Now",
                            style = Typography.headlineMedium,
                            fontWeight = FontWeight.Normal
                        )
                        Text(
                            currentWeatherData.weather.firstOrNull()?.main ?: "none",
                            style = Typography.headlineSmall,
                            fontWeight = FontWeight.Normal
                        )
                    }
                    if (currentWeatherData.name == "Globe") {
                        Text(
                            "You are not using accurate location, please open your GPS/Location",
                            style = Typography.bodySmall,
                            modifier = Modifier
                                .padding(start = 10.dp, end = 10.dp)
                        )
                    }
                    val WeatherDetails = getCurrentWeatherDetails(currentWeatherData)

                    Column() {
                        //row 1
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            InfoCard(
                                icon = R.drawable.weather_clear,
                                header = "Humidity",
                                message = WeatherDetails.humidityMessage,
                                dataval = currentWeatherData.main.humidity.toString(),
                                modifier = Modifier.weight(1f)
                            )
                            InfoCard(
                                icon = R.drawable.weather_clear,
                                header = "Feels Like",
                                message = WeatherDetails.feelsLikeMessage,
                                dataval = currentWeatherData.main.feels_like.toString(),
                                modifier = Modifier.weight(1f)
                            )
                        }


                        //row 2
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            InfoCard(
                                icon = R.drawable.weather_clear,
                                header = "Visibility",
                                message = WeatherDetails.visibilityMessage,
                                dataval = currentWeatherData.visibility.toString(),
                                modifier = Modifier.weight(1f)
                            )
                            InfoCard(
                                icon = R.drawable.weather_clear,
                                header = "Wind",
                                message = WeatherDetails.windMessage,
                                dataval = currentWeatherData.wind.speed.toString(),
                                modifier = Modifier.weight(1f)
                            )
                        }

//row 3
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            InfoCard(
                                icon = R.drawable.weather_clear,
                                header = "Rain",
                                message = WeatherDetails.rainMessage,
                                dataval = currentWeatherData.rain?.`1h`?.toString() ?: "0.0",
                                modifier = Modifier.weight(1f)

                            )
                            InfoCard(
                                icon = R.drawable.weather_clear,
                                header = "Sea Level",
                                message = WeatherDetails.seaLevelPressureMessage,
                                dataval = currentWeatherData.main.sea_level.toString(),
                                modifier = Modifier.weight(1f)
                            )
                        }
//row 4
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            InfoCard(
                                icon = R.drawable.weather_clear,
                                header = "Cloud",
                                message = WeatherDetails.cloudMessage,
                                dataval = currentWeatherData.clouds.all.toString(),
                                modifier = Modifier.weight(1f)

                            )
                            InfoCard(
                                icon = R.drawable.weather_clear,
                                header = "Air Press.",
                                message = WeatherDetails.airPressureMessage,
                                dataval = currentWeatherData.main.pressure.toString(),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        HorizontalDivider(
                            modifier = Modifier
                                .padding(20.dp),
                            thickness = 2.dp,
                            color = Color.LightGray
                        )

                        //Recommendation
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 15.dp),
                        ) {
                            Text(
                                "Field Insights",
                                style = Typography.headlineMedium,
                                fontWeight = FontWeight.Normal
                            )
                        }
                        /*
                            weatherMain: String?,
                            temperature: Double?,
                            humidity: Double?,
                            windSpeed: Double?,
                            rainfall: Double?,
                            dt: Long?

                         */

                        Column(
                            modifier = Modifier.padding(horizontal = 15.dp, vertical = 5.dp)
                        ) {
                            val recommendation = getWeatherRecommendation(
                                weatherMain = currentWeatherData.weather.firstOrNull()?.main,
                                temperature = currentWeatherData.main.temp,
                                humidity = currentWeatherData.main.humidity,
                                windSpeed = currentWeatherData.wind.speed,
                                rainfall = currentWeatherData.rain.`1h`, // Type conversion
                                dt = currentWeatherData.dt.toLong() // Safe call with ?
                            )


                            Text(recommendation.farming, style = Typography.bodyLarge)
                            Text(recommendation.farmerHealth, style = Typography.bodyLarge)
                            Text(recommendation.pestManagement, style = Typography.bodyLarge)

                        }

                    }
                }
            }


            errorMessage != null -> {
                Text(text = "Error: $errorMessage")
            }

            else -> {
                Text(text = "No data available", modifier = Modifier.fillMaxSize())
            }

        }
    }
}