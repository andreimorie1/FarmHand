package com.example.farmhand.weather.models

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

class LocationManager (
    private val context: Context
) {

    private var fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private var locationCallback: LocationCallback? = null

    // State to hold the current location
    val currentLocation = mutableStateOf<Location?>(null)

    // Function to request location access
    suspend fun requestLocationAccess(): Boolean {
        return if (hasLocationPermission()) {
            startLocationUpdates()
            true
        } else {
            false // Permissions not granted; return false
        }
    }

    // Check for location permissions
    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

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
                // Handle the security exception if permissions aren't granted
                Log.e("LocationManager", "Permission not granted: ${e.message}")
            }
        } else {
            // You might want to request permission here if it's not granted
            Log.e("LocationManager", "Location permission not granted.")
        }
    }

    // Stop location updates
    fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
    }
}