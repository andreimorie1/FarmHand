package com.example.farmhand.module_health.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.example.farmhand.module_health.models.IdentificationScreen
import com.example.farmhand.ui.theme.Typography

@Composable
fun PlantHealthScreen(

    context: Context = LocalContext.current
) {
    // Holds the captured or selected image URI
    val capturedImageUri = remember { mutableStateOf<Uri?>(null) }
    val permissionGranted = remember { mutableStateOf(false) }


    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        permissionGranted.value = isGranted
    }


    // Checking if camera permission is already granted
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            permissionGranted.value = true
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Plant Health Screen", style = Typography.displaySmall)

        // Integrating IdentificationScreen for image capture/selection
        IdentificationScreen(
            onImageCapture = { uri ->
                capturedImageUri.value = uri
                // You can also handle additional image processing here if needed
            }
        )

        Spacer(
            modifier = Modifier
                .height(16.dp)
        )

            // Display the captured or selected image if available
            capturedImageUri.value?.let { uri ->
                Text("Preview", style = Typography.bodyMedium, modifier = Modifier.padding(vertical = 5.dp))
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Captured or Selected Image",
                    modifier = Modifier
                        .size(200.dp)
                        .border(1.dp, MaterialTheme.colorScheme.onBackground)
                )
            }

    }
}
