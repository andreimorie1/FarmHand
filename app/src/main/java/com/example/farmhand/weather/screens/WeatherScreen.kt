package com.example.farmhand.weather.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.farmhand.ui.theme.Typography
import com.example.farmhand.weather.models.WeatherViewModel
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    context: Context = LocalContext.current
) {
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationPermissionGranted = remember { mutableStateOf(false) }
    val apiKey = "6fdf4fe4fd4831597e9eedf198a0eeaa"

    val isFetchingData by viewModel.isFetchingData.collectAsState()


    val showPermissionDialog = remember { mutableStateOf(false) }


    // Get permission for current location
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            showPermissionDialog.value = true // Show dialog if permission is not granted
        } else {
            locationPermissionGranted.value = true
        }
    }
    // Show permission dialog if needed
    if (showPermissionDialog.value) {
        PermissionDialog(
            onDismiss = { showPermissionDialog.value = false },
            onConfirm = {
                // Request permission
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
                showPermissionDialog.value = false
            }
        )
    }



    // Get weather data after permission is granted
    LaunchedEffect(locationPermissionGranted.value) {
        if (locationPermissionGranted.value) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    viewModel.updateLocation(
                        it.latitude,
                        it.longitude
                    ) // Update ViewModel with location

                    // Fetch weather data using the ViewModel
                    viewModel.refreshWeatherData(apiKey = apiKey)
                    Log.d("WeatherScreen", "Launched effect -- Latitude: ${it.latitude}, Longitude: ${it.longitude}")
                }
            }
        }
    }


    //  Testing for location access
    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
        if (location != null) {
            Log.d("Location", "Lat: ${location.latitude}, Long: ${location.longitude}")
        } else {
            Log.e("Location", "Failed to get location")
        }
    }


    PullToRefreshBox(
        isRefreshing = isFetchingData,
        onRefresh = {
            viewModel.refreshWeatherData(apiKey) // Trigger the data refresh
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

