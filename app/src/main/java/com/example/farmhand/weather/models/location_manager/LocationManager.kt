package com.example.farmhand.weather.models.location_manager

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.example.farmhand.Manifest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class LocationManager(private val context: Context) {

    fun getFineLocation(callback: (Double?, Double?) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        callback(location.latitude, location.longitude)
                    } else {
                        callback(null, null) // Fallback if location is null
                    }
                }
                .addOnFailureListener {
                    callback(null, null) // Error occurred, fallback
                }
        } else {
            callback(null, null) // Fine location not available, fallback
        }
    }
}
