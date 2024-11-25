package com.example.farmhand.module_Reco.model

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmhand.module_Reco.API.OpenAiRepository
import com.example.farmhand.module_Reco.API.data.OpenAiRequest
import com.example.farmhand.module_Reco.API.data.OpenAiResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class OpenAiViewModel @Inject constructor(
    private val repository: OpenAiRepository
) : ViewModel() {

    var openAiResponse: OpenAiResponse? by mutableStateOf(null)
        private set

    var responseText: String? by mutableStateOf(null)
        private set

    var errorMessage: String? by mutableStateOf(null)
        private set

    var isFetchingData: Boolean by mutableStateOf(false)
        private set


    // Function to create OpenAI completion
    fun createCompletion(request: OpenAiRequest) {
        Log.d("OpenAiViewModel", "createCompletion called with request: $request")

        isFetchingData = true

        viewModelScope.launch {
            Log.d("OpenAiViewModel", "Fetching data started")

            val result = repository.createCompletion(request)
            result.onSuccess {
                Log.d("OpenAiViewModel", "Data fetch successful: $it")

                openAiResponse = it
                responseText = it.choices.firstOrNull()?.message?.content ?: "No text found"
                errorMessage = null
            }.onFailure {
                Log.e("OpenAiViewModel", "Data fetch failed: ${it.localizedMessage}", it)
                handleFetchError(it)
            }.also {
                isFetchingData = false
            }
        }
    }

    // Error handler
    private fun handleFetchError(exception: Throwable) {
        errorMessage = exception.localizedMessage ?: "Unknown error occurred"
    }
}

