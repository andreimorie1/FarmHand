package com.example.farmhand.module_health.models

import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/*
1. CaptureArea
 */

@Composable
fun IdentificationScreen(
    onImageCapture: (Uri) -> Unit
) {
    val context = LocalContext.current
    val imageUri = remember { mutableStateOf<Uri?>(null) }

    // Launcher for selecting an image from the gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            onImageCapture(it)
            imageUri.value = it
        }
    }

    // Launcher for capturing a new image with the camera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageUri.value?.let { onImageCapture(it) }
        }
    }

    // Temporary URI for the image to be captured
    val tempImageUri = remember { mutableStateOf<Uri?>(null) }

    // Dialog or buttons to choose between camera and gallery
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Choose an option", style = MaterialTheme.typography.titleLarge)
                    HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(horizontal = 30.dp), color = MaterialTheme.colorScheme.error)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(onClick = {
                        showDialog = false
                        // Set up the content values for storing the image in MediaStore
                        val contentValues = ContentValues().apply {
                            put(MediaStore.Images.Media.DISPLAY_NAME, "captured_image.jpg")
                            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyApp")
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

    CaptureArea(
        onImageCapture = {
            showDialog = true
        }
    )
}


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


