package com.example.farmhand.module_weather.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.farmhand.module_weather.api.data.CurrentWeather.CurrentWeatherResponse
import com.example.farmhand.module_weather.api.data.ThirtyDayWeather.ThirtyDayForecastResponse
import com.example.farmhand.module_weather.models.LocationManager
import com.example.farmhand.module_weather.utils.CurrentWeatherDetails
import com.example.farmhand.module_weather.utils.ForecastLazyRow
import com.example.farmhand.module_weather.utils.WeatherHeader
import com.example.farmhand.module_weather.utils.getCurrentWeatherDetails
import com.example.farmhand.module_weather.utils.getWeatherRecommendation


@Composable
fun WeatherContent(
    isFetchingData: Boolean,
    currentWeatherData: CurrentWeatherResponse?,
    thirtyDayForecastData: ThirtyDayForecastResponse?,
    errorMessage: String?,
) {
    if (isFetchingData) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "Fetching Weather Data",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    } else {
        when {
            currentWeatherData != null && thirtyDayForecastData != null -> {
                val WeatherDetails = getCurrentWeatherDetails(currentWeatherData)
                val recommendation = getWeatherRecommendation(currentWeatherResponse = currentWeatherData)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .background( //main background
                            color = MaterialTheme.colorScheme.surface
                        ),
                    verticalArrangement = Arrangement.Top,
                ) {
                    //Header
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 15.dp, vertical = 10.dp)
                    ) {
                        //Header Composable
                        WeatherHeader(currentWeatherData)
                    }
                    if (currentWeatherData.name == "Globe") {
                        Text(
                            "You are not using accurate location, please open your GPS/Location",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .padding(horizontal = 15.dp)
                        )
                    }
                    //30 Day Forecast Cards
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
/* ?????
                        Text(
                            "Upcoming Weathers",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(start = 10.dp)
                        )
 */
                        //  Forecast lazy row composable
                        ForecastLazyRow(thirtyDayForecastData)
                    }
                    // Today's Highlights Card
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(horizontal = 60.dp, vertical = 3.dp))
                        Text(
                            "Today's Highlights",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .padding(vertical = 5.dp)
                        )
                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(horizontal = 60.dp, vertical = 3.dp))

                        // Weather Details Card
                        CurrentWeatherDetails(
                            currentWeatherData = currentWeatherData,
                            WeatherDetails = WeatherDetails
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(horizontal = 60.dp, vertical = 3.dp))

                        Text(
                            "Farming Insights",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .padding(vertical = 5.dp)
                        )
                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(horizontal = 60.dp, vertical = 3.dp))

                    }

                    Surface(
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(vertical = 5.dp, horizontal = 20.dp)
                            ,
                        tonalElevation = 10.dp,
                        shadowElevation = 10.dp,
                        shape = MaterialTheme.shapes.large,

                        ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceContainer),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                "Farming",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .padding(start = 10.dp, top = 10.dp)
                            )
                            Text(
                                recommendation.farming,
                                style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Left),
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            )

                            Text(
                                "Farmer Wellness & Safety",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .padding(start = 10.dp, top = 10.dp)
                            )
                            Text(
                                recommendation.farmerHealth,
                                style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Left),
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            )

                            Text(
                                "Crop Protection",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .padding(start = 10.dp, top = 10.dp)
                            )
                            Text(
                                recommendation.pestManagement,
                                style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Left),
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
            errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                ) {
                    Text(text = "Error: $errorMessage")
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                ) {
                    Text(text = "No data available", modifier = Modifier.fillMaxSize())
                }
            }

        }
    }
}