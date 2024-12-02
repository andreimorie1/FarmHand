package com.example.farmhand.module_health.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.farmhand.module_Reco.model.OpenAiViewModel
import com.example.farmhand.module_health.api.data.post_identification.actual.CreateidentificationRequest
import com.example.farmhand.module_health.models.KindwiseViewModel
import com.example.farmhand.module_health.utils.CapturedData
import com.example.farmhand.module_health.utils.IdentificationScreen
import com.example.farmhand.module_health.utils.TutorialColumn
import com.example.farmhand.module_weather.models.WeatherViewModel

@Composable
fun PlantHealthScreen(
    weatherViewModel: WeatherViewModel,
    KindwiseViewModel: KindwiseViewModel,
    OpenAiViewModel: OpenAiViewModel,
    context: Context = LocalContext.current
) {
    val scrollState = rememberScrollState()
    val capturedImageUris = remember { mutableStateOf<List<Uri>>(emptyList()) }
    val isLoading by KindwiseViewModel.isFetchingData.collectAsState()

    // Camera permission handling
    val cameraPermission = Manifest.permission.CAMERA
    val permissionGranted = remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted.value = isGranted
        if (!isGranted) {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Check if permission is already granted
    LaunchedEffect(Unit) {
        permissionGranted.value = ContextCompat.checkSelfPermission(context, cameraPermission) ==
                PackageManager.PERMISSION_GRANTED
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TutorialColumn()

        // Call IdentificationScreen and pass the state
        IdentificationScreen(
            onImageCapture = { newUris ->
                // Append new URIs to the existing list, ensuring no duplicates
                capturedImageUris.value += newUris.filter { it !in capturedImageUris.value }
            }
        )

        // If camera permission is not granted, show a button to request permission
        if (!permissionGranted.value) {
            Button(
                onClick = {
                    permissionLauncher.launch(cameraPermission)
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Grant Camera Permission")
            }
        } else {
            // Allow capturing or processing images if permission is granted
            if (capturedImageUris.value.isNotEmpty()) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onBackground,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(40.dp)
                    )
                } else {
                    Button(
                        onClick = {
                            // Convert image to base64
                            val base64Images = KindwiseViewModel.encodeImagesToBase64(
                                uris = capturedImageUris.value,
                                context = context
                            )

                            // Create the request object
                            val request = CreateidentificationRequest(
                                images = base64Images,
                                latitude = weatherViewModel.currentLocation.value?.latitude ?: 0.0,
                                longitude = weatherViewModel.currentLocation.value?.longitude ?: 0.0,
                                similar_images = true
                            )
                            KindwiseViewModel.createIdentification(request)
                        }
                    ) {
                        Text("Check")
                    }
                }
            }
        }

        if (KindwiseViewModel.currentIdentificationData != null) {
            CapturedData(
                kindwiseViewModel = KindwiseViewModel,
                openAiViewModel = OpenAiViewModel,
                weatherViewModel = weatherViewModel
            )
        }
    }
}


