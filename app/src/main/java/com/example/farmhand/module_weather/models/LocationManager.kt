package com.example.farmhand.module_weather.models

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import javax.inject.Inject

class LocationManager @Inject constructor(
    private val context: Context
) {
    private var fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private var locationCallback: LocationCallback? = null

    // State to hold the current location
    val currentLocation = mutableStateOf<Location?>(null)

    // Function to check if location permission is granted
    fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    // Function to start location updates
    fun startLocationUpdates() {
        if (hasLocationPermission()) {
            try {
                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        for (location in locationResult.locations) {
                            currentLocation.value = location
                        }
                    }
                }

                val locationRequest =
                    LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                        .setMinUpdateIntervalMillis(5000)
                        .setWaitForAccurateLocation(false)
                        .build()

                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback!!,
                    null
                )
            } catch (e: SecurityException) {
                Log.e("LocationManager", "Permission not granted: ${e.message}")
            }
        } else {
            Log.e("LocationManager", "Location permission not granted.")
        }
    }

    // Function to stop location updates
    fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
    }
}
