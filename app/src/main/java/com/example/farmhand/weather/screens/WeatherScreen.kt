package com.example.farmhand.weather.screens

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.example.farmhand.ui.theme.Typography
import com.example.farmhand.weather.models.WeatherViewModel
import com.example.farmhand.weather.models.LocationManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    context: Context = LocalContext.current
) {

    val locationManager = remember { LocationManager(context) } // Initialize LocationManager
    var hasLocationPermission by remember { mutableStateOf(locationManager.hasLocationPermission()) }
    val currentLocation by remember { locationManager.currentLocation }

    val apiKey = "6fdf4fe4fd4831597e9eedf198a0eeaa"
    val isFetchingData by viewModel.isFetchingData.collectAsState()
    var hasFetchedWeatherData by remember { mutableStateOf(false) }


    // Permission launcher for location permission request
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
        if (isGranted) {
            locationManager.startLocationUpdates()
        }
    }

    // Check and request location permission
    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            locationManager.startLocationUpdates()
        }
    }

    // Fetch weather data based on permission and location updates
    LaunchedEffect(hasLocationPermission, currentLocation) {
        if (hasLocationPermission && currentLocation != null && !hasFetchedWeatherData) {
            viewModel.refreshWeatherData(
                longitude = currentLocation?.longitude ?: 0.0,
                latitude = currentLocation?.latitude ?: 0.0,
                apiKey = apiKey
            )
            hasFetchedWeatherData = true // Set the flag to true to prevent further fetches
        }
    }

    PullToRefreshBox(
        isRefreshing = isFetchingData,
        onRefresh = {
            viewModel.refreshWeatherData(
                longitude = currentLocation?.longitude ?:0.0,
                latitude = currentLocation?.latitude ?:0.0,
                apiKey = apiKey
            ) // Trigger the data refresh
        },
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            if (isFetchingData) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Fetching Weather Data",
                        style = Typography.titleMedium,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else {
                when {
                    // Check if currentWeather is populated
                    viewModel.currentWeatherData != null -> {
                        val currentWeather = viewModel.currentWeatherData

                        Text("City name: ${currentWeather?.name}")
                        Text("Object: ${currentWeather?.weather?.firstOrNull()?.main}")
                        Text("Description: ${currentWeather?.weather?.firstOrNull()?.description}")
                        Text("Temperature: ${currentWeather?.main?.temp}°C")
                        Text("Humidity: ${currentWeather?.main?.humidity}%")
                        Text("Condition: ${currentWeather?.weather?.firstOrNull()?.description}")
                        Text("Wind: ${currentWeather?.wind?.speed} m/s")
                    }
                    viewModel.errorMessage != null -> {
                        Text(text = "Error: ${viewModel.errorMessage}")
                    }
                    else -> {
                        Text(text = "No data available")
                    }
                }
            }
        }
    }
}

