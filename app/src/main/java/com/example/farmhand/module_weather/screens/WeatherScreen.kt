package com.example.farmhand.module_weather.screens

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.farmhand.module_weather.models.LocationManager
import com.example.farmhand.module_weather.models.WeatherViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    context: Context = LocalContext.current
) {

    val locationManager = remember { LocationManager(context) }
    var hasLocationPermission by remember { mutableStateOf(locationManager.hasLocationPermission()) }
    val currentLocation by remember { locationManager.currentLocation }

    val apiKey = "5e970ffe1ccf427cc2885d907f84e683"
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

    //check if location has permission
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            locationManager.startLocationUpdates()
        }
    }

    // Check and request location permission
    LaunchedEffect(Unit) {
        if (viewModel.fiveDayForecastData == null && viewModel.currentWeatherData == null) {
            if (!hasLocationPermission) {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                locationManager.startLocationUpdates()
            }
            viewModel.refreshWeatherData(
                longitude = currentLocation?.longitude ?: 0.0,
                latitude = currentLocation?.latitude ?: 0.0,
                apiKey = apiKey
            )
        }
    }

    // Fetch module_weather data based on permission and location updates
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
                longitude = currentLocation?.longitude ?: 0.0,
                latitude = currentLocation?.latitude ?: 0.0,
                apiKey = apiKey
            ) // Trigger the data refresh
        },
        modifier = Modifier.fillMaxSize()
    ) {
        WeatherContent(
            isFetchingData = isFetchingData,
            currentWeatherData = viewModel.currentWeatherData,
            thirtyDayForecastData = viewModel.thirtyDayForecastData,
            errorMessage = viewModel.errorMessage,
        )
    }
}