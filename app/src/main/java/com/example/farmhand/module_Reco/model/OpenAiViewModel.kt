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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class OpenAiViewModel @Inject constructor(
    private val repository: OpenAiRepository
) : ViewModel() {

    var OpenAiResponse: OpenAiResponse? by mutableStateOf(null)
        private set

    var errorMessage: String? by mutableStateOf(null)
        private set

    private val _isFetchingData = MutableStateFlow(false)
    val isFetchingData: StateFlow<Boolean> = _isFetchingData

    // Function to create OpenAI completion
    fun createCompletion(request: OpenAiRequest) {
        Log.d("OpenAiViewModel", "createCompletion called with request: $request")

        _isFetchingData.value = true

        viewModelScope.launch {
            Log.d("OpenAiViewModel", "Fetching data started")

            val result = repository.createCompletion(request)
            result.onSuccess {
                Log.d("OpenAiViewModel", "Data fetch successful: $it")

                OpenAiResponse = it
                errorMessage = null
            }.onFailure {
                Log.e("OpenAiViewModel", "Data fetch failed: ${it.localizedMessage}", it)

                handleFetchError(it)
            }.also {
                _isFetchingData.value = false
            }
        }
    }

    // Error handler
    private fun handleFetchError(exception: Throwable) {
        errorMessage = exception.localizedMessage ?: "Unknown error occurred"
    }
}

