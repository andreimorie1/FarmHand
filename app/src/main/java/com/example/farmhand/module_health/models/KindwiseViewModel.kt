package com.example.farmhand.module_health.models

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmhand.module_health.api.KindwiseRepository
import com.example.farmhand.module_health.api.data.post_identification.actual.ActualIdentificationResponse
import com.example.farmhand.module_health.api.data.post_identification.actual.CreateidentificationRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class KindwiseViewModel @Inject constructor(
    private val repository: KindwiseRepository
) : ViewModel() {

    //mock data
    var currentIdentificationData: ActualIdentificationResponse? by mutableStateOf(null)

    var errorMessage: String? by mutableStateOf(null)
    private val _isFetchingData = MutableStateFlow(false)
    val isFetchingData: StateFlow<Boolean> = _isFetchingData


    // Function to create identification (real or mock)
    fun createIdentification(
        request: CreateidentificationRequest,
    ) {
        _isFetchingData.value = true

        viewModelScope.launch {
            val result = repository.createIdentification(request)
            result.onSuccess {
                Log.d("KindwiseViewModel", "Identification Request Success: \n$it")
                currentIdentificationData = it
                errorMessage = null
            }.onFailure {
                Log.d("KindwiseViewModel", "Identification Request Error: $it")
                handleFetchError(it)
            }.also {
                delay(1000)
                _isFetchingData.value = false
            }
        }
    }

    // Function to get identification (real or mock)
    fun getIdentification(
        accessToken: String,
        onResult: (Result<ActualIdentificationResponse>) -> Unit
    ) {
        _isFetchingData.value = true

        viewModelScope.launch {
            val result = repository.getIdentification(accessToken)
            result.onSuccess {
                Log.d("KindwiseViewModel", "Identification Request Success: $it")
                currentIdentificationData = it
                errorMessage = null
            }.onFailure {
                Log.d("KindwiseViewModel", "Identification Request Error: $it")
                handleFetchError(it)
            }.also {
                delay(1000)
                _isFetchingData.value = false
            }
        }
    }

    // Function to encode images to Base64
    fun encodeImagesToBase64(uris: List<Uri>, context: Context): List<String> {
        return uris.mapNotNull { uri ->
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()
                bytes?.let { Base64.encodeToString(it, Base64.DEFAULT) }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    // Handle specific error scenarios
    private fun handleFetchError(throwable: Throwable) {
        when (throwable) {
            is UnknownHostException -> {
                Log.e("KindwiseViewModel", "No internet connection: ${throwable.message}")
                errorMessage =
                    "No internet connection detected. Please ensure you are connected to the internet to access crop health services."
            }

            is SocketTimeoutException -> {
                Log.e("KindwiseViewModel", "Connection timeout: ${throwable.message}")
                errorMessage =
                    "The request timed out. Crop health data could not be retrieved. Please check your connection and try again."
            }

            is SecurityException -> {
                Log.e("KindwiseViewModel", "Location permission denied: ${throwable.message}")
                errorMessage =
                    "Location access is denied. Location is required to analyze regional crop health. Please enable location permissions in your settings."
            }

            else -> {
                Log.e("KindwiseViewModel", "Error fetching crop health data: ${throwable.message}")
                errorMessage =
                    "Unable to fetch crop health information at the moment. Please try again later or contact support if the issue persists."
            }
        }
    }

}
//create requests