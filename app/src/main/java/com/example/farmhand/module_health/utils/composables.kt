package com.example.farmhand.module_health.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.farmhand.module_Reco.API.data.Message
import com.example.farmhand.module_Reco.API.data.OpenAiRequest
import com.example.farmhand.module_Reco.model.OpenAiViewModel
import com.example.farmhand.module_health.models.KindwiseViewModel
import com.example.farmhand.module_weather.models.WeatherViewModel
import com.example.farmhand.ui.theme.Typography
import dev.jeziellago.compose.markdowntext.MarkdownText

/*
1. IdentificationScreen
2. CaptureArea
3. Tutorial Column
4. Captured Data
 */

//IdentificationScreen
@Composable
fun IdentificationScreen(
    onImageCapture: (List<Uri>) -> Unit
) {
    val context = LocalContext.current
    val imageUris = remember { mutableStateOf<List<Uri>>(emptyList()) }
    // Temporary URI for the image to be captured
    val tempImageUri = remember { mutableStateOf<Uri?>(null) }

    // Launcher for selecting an image from the gallery (multiple)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            imageUris.value += uris // Append new URIs
            onImageCapture(imageUris.value)
        }
    }


    // Launcher for capturing a new image with the camera (single image at a time)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempImageUri.value?.let {
                imageUris.value += it // Append the captured URI
                onImageCapture(imageUris.value)
                Log.d("IdentificationScreen", "Image captured successfully, URI: $it")
            } ?: Log.d("IdentificationScreen", "No image URI found after capture")
        } else {
            Log.d("IdentificationScreen", "Image capture failed")
        }
    }

    // Dialog or buttons to choose between camera and gallery
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Choose an option", style = MaterialTheme.typography.titleLarge)
                    HorizontalDivider(
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 30.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(onClick = {
                        showDialog = false
                        // Set up the content values for storing the image in MediaStore
                        val contentValues = ContentValues().apply {
                            put(
                                MediaStore.Images.Media.DISPLAY_NAME,
                                "captured_image_${System.currentTimeMillis()}.jpg"
                            )
                            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyApp/.private")
                        }

                        // Insert a new image entry and retrieve the URI
                        val uri = context.contentResolver.insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            contentValues
                        )

                        if (uri != null) {
                            tempImageUri.value = uri
                            cameraLauncher.launch(uri)
                        } else {
                            Log.e("IdentificationScreen", "Failed to create MediaStore URI")
                        }
                    }) {
                        Text("Take a Picture")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        showDialog = false
                        galleryLauncher.launch("image/*")
                    }) {
                        Text("Select from Gallery")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }

    // UI component to trigger the dialog
    CaptureArea(
        onImageCapture = {
            showDialog = true
        }
    )

    // Display a message when no images are available
    if (imageUris.value.isEmpty()) {
        Text(
            text = "Check the state of your plants",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 10.dp)
        )
    } else {
        // Display preview images and delete functionality, always rendered
        Text("Preview", style = Typography.bodyMedium, modifier = Modifier.padding(vertical = 5.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 5.dp)
        ) {
            items(imageUris.value) { uri ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Captured/Selected Image",
                        modifier = Modifier
                            .size(150.dp)
                            .border(1.dp, MaterialTheme.colorScheme.onBackground)
                    )
                    Button(
                        onClick = {
                            deleteImageFromDevice(context, uri)
                            // Remove the URI from the list after deletion
                            imageUris.value -= uri
                        },
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text("Delete", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

// Helper function to delete an image from the device
private fun deleteImageFromDevice(context: Context, uri: Uri) {
    try {
        // Delete the image from MediaStore using ContentResolver
        val deleted = context.contentResolver.delete(uri, null, null)
        if (deleted > 0) {
            Log.d("IdentificationScreen", "Image deleted successfully: $uri")
        } else {
            Log.e("IdentificationScreen", "Failed to delete image: $uri")
        }
    } catch (e: Exception) {
        Log.e("IdentificationScreen", "Error while deleting image: ${e.message}")
    }
}

//CaptureArea
@Composable
fun CaptureArea(onImageCapture: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable { onImageCapture() } // Trigger the image capture dialog
    ) {
        Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = "Capture Image",
            modifier = Modifier
                .align(Alignment.Center)
                .size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Tap to capture or upload an image",
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(8.dp)
        )
    }
}

// Tutorial Column
@Composable
fun TutorialColumn() {
    // Tutorial Column
    Column(
        modifier = Modifier.padding(horizontal = 10.dp)
    ) {
        Text(
            text = "Step 1: Prepare Your Crop",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Step 2: Take a clear multiple photo of your crop or choose an existing image from your gallery.",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

//  Captured Data
@Composable
fun CapturedData(
    kindwiseViewModel: KindwiseViewModel,
    openAiViewModel: OpenAiViewModel,
    weatherViewModel: WeatherViewModel
) {

    val weatherData = weatherViewModel.currentWeatherData
    val currentIdentificationData = kindwiseViewModel.currentIdentificationData

    val openAiResponse = openAiViewModel.openAiResponse
    val responseText = openAiViewModel.responseText

    fun Double.format(digits: Int) = "%.${digits}f".format(this)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Plant Health Analysis",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 2.dp)
        )

        currentIdentificationData?.result?.let { result ->
            // Display if it's a plant
            Column(
                Modifier
                    .wrapContentSize()
            ) {
                Text(
                    text = if (result.is_plant.binary) {
                        "This is a plant (Confidence: %.2f%%)".format(result.is_plant.probability * 100)
                    } else {
                        "No plant detected"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                Text(
                    text = if (result.is_plant.binary) {
                        "Detected Plant: ${result.crop.suggestions.firstOrNull()?.name}"
                    } else {
                        "No plant detected"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                // Display disease suggestions
                result.disease.suggestions.forEach { suggestion ->
                    var showDialog by remember { mutableStateOf(false) }

                    Card(
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            Text(
                                text = "Disease: ${suggestion.name}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Scientific Name: ${suggestion.scientific_name}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Probability: ${(suggestion.probability * 100).format(2)}%",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            // Display similar images
                            suggestion.similar_images.forEach { image ->
                                AsyncImage(
                                    model = image.url,
                                    contentDescription = "Similar image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Source: ${image.citation}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.Gray
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val unhealthyPrompt = """Current weather conditions:
Rain: ${weatherData?.rain?.`1h` ?: 0.0}mm in the last hour
Temperature: ${weatherData?.main?.temp}°C (feels like ${weatherData?.main?.feels_like}°C)
Humidity: ${weatherData?.main?.humidity}%
Wind Speed: ${weatherData?.wind?.speed}m/s

Identified crop:
Name: ${result.crop.suggestions.firstOrNull()?.name} (scientific name: ${result.crop.suggestions.firstOrNull()?.scientific_name})
Disease/Pest: ${suggestion.name} (scientific name: ${suggestion.scientific_name})

Consider the following when recommending control measures:
1. The weather conditions (rain, temperature, humidity, wind speed) and how they affect pest behavior and disease spread in tropical climates like the Philippines.
2. Local agricultural practices for pest and disease management, including organic and chemical methods, as well as integrated pest management strategies.

Please format the response as follows:
Details of the Pest/Disease: symptoms, effects, lifecycle spread (if relevant), and how it relates to the weather.
Control Measures: A list of actionable steps (in bullet points).
Weather Considerations: How the current weather affects pest behavior and disease spread in the Philippines.
Additional Notes: Any relevant local practices or methods.

Based on the current weather conditions and the crop health status, please take your time to carefully consider and recommend the most appropriate control measures for the identified disease/pest, tailored to the farming conditions in the Philippines."""
                                    .trimIndent()
                                val healthyPrompt = """Current weather conditions:
Rain: ${weatherData?.rain?.`1h` ?: 0.0}mm in the last hour
Temperature: ${weatherData?.main?.temp}°C (feels like ${weatherData?.main?.feels_like}°C)
Humidity: ${weatherData?.main?.humidity}%
Wind Speed: ${weatherData?.wind?.speed}m/s

Identified crop:
Name: ${result.crop.suggestions.firstOrNull()?.name} (scientific name: ${result.crop.suggestions.firstOrNull()?.scientific_name})

Your plants are healthy! Please consider the following preventive measures to maintain their health:

1. The weather conditions (rain, temperature, humidity, wind speed) and how they might influence potential risks such as pest infestations, fungal diseases, or crop stress in tropical climates like the Philippines.
2. Local agricultural best practices for preventive care, including fertilization schedules, irrigation, and seasonal adjustments.

Please format the response as follows:
Plant Health Status: A brief affirmation that the plants are in good condition.
Preventive Maintenance: A list of best practices to maintain plant health (in bullet points).
Weather Considerations: How the current weather may influence potential risks and how to adapt preventive measures.
Additional Notes: Any relevant local farming techniques or cultural practices that promote plant health.

Based on the current weather conditions and the healthy status of the crop, provide recommendations for continued maintenance and prevention tailored to tropical farming conditions in the Philippines."""
                                    .trimIndent()
                                Button(
                                    onClick = {// CALL OPENAI!!
                                        showDialog = true

                                        val prompt =
                                            if (result.crop.suggestions.firstOrNull()?.name != "healthy") unhealthyPrompt else healthyPrompt

                                        val messages = listOf(
                                            Message(
                                                role = "system",
                                                content = "You are an expert in Filipino agriculture and pest/disease management."
                                            ),
                                            Message(role = "user", content = prompt)
                                        )
                                        val request = OpenAiRequest(
                                            messages = messages,
                                        )

                                        openAiViewModel.createCompletion(request = request)
                                    }
                                ) {
                                    Text("See Solutions")
                                }
                            }
                        }
                    }
                    //result.crop.suggestions.firstOrNull()?.scientific_name ?: ""  NAME OF DETECTED CROP
                    //suggestion.name NAME OF DETECTED PEST/DISEASE
                    CustomDialog(
                        showDialog = showDialog,
                        onDismiss = { showDialog = false },
                        titleContent = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (suggestion.name == "healthy" || suggestion.name == "Healthy") {
                                    Text(
                                        "A healthy ${result.crop.suggestions.firstOrNull()?.name ?: ""}",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        result.crop.suggestions.firstOrNull()?.scientific_name
                                            ?: "",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                } else {
                                    Text(
                                        suggestion.name,
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    Text(
                                        suggestion.scientific_name,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }
                                HorizontalDivider(
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(
                                        horizontal = 30.dp,
                                        vertical = 3.dp
                                    ),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        bodyContent = {
                            val scrollState = rememberScrollState()

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .verticalScroll(scrollState),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Show loading indicator if fetching data
                                if (openAiViewModel.isFetchingData) {
                                    Text(
                                        text = "Fetching data...",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                    CircularProgressIndicator() // Show loading state
                                } else if (openAiViewModel.responseText != null) {
                                    // Display the OpenAI response text
                                    MarkdownText(
                                        markdown = responseText ?: "No response received",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                } else {
                                    // Handle error state or no data
                                    Text(text = "Error or no response.")
                                }
                            }
                        },
                        confirmButtonText = "close"
                    )
                }
            }
        } ?: Text(text = "Unknown error", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun CustomDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    titleContent: @Composable () -> Unit,
    bodyContent: @Composable () -> Unit,
    confirmButtonText: String = "Close",
) {
    val scrollState: ScrollState = rememberScrollState()
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { titleContent() },
            text = {
                bodyContent()
                Column(Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .verticalScroll(scrollState)
                ) {

                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDismiss()
                    }
                ) {
                    Text(confirmButtonText)
                }
            }
        )
    }
}
