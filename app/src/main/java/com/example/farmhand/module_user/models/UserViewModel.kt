package com.example.farmhand.module_user.models

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmhand.module_user.api.ApiResponse
import com.example.farmhand.module_user.api.IdRequest
import com.example.farmhand.module_user.api.LoginRequest
import com.example.farmhand.module_user.api.RegisterRequest
import com.example.farmhand.module_user.api.UpdateRequest
import com.example.farmhand.module_user.api.UserRepository
import com.example.farmhand.navigation.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: UserRepository
) : ViewModel() {

    var response: ApiResponse? by mutableStateOf(null)
    var isFetchingData: Boolean by mutableStateOf(false)

    var errorMessage: String? = null

    fun resetData() {
        response = null
        errorMessage = null
    }
    fun resetErrorMessage() {
        errorMessage = null
    }

    fun onLogOutNew() {
        response = null
        AuthManager.logout(context)
        Log.d("uAccountModel", "User logged out ${AuthManager.isUserLoggedIn(context)}")
    }

    // Function to register a farmer
    fun registerFarmer(request: RegisterRequest) {
        Log.d("UserViewModel", "registerFarmer called with request: $request")
        errorMessage = null

        viewModelScope.launch {
            Log.d("UserViewModel", "Registration process started")

            isFetchingData = true
            val result = repository.registerFarmer(request)
            result.onSuccess {
                Log.d("UserViewModel", "Registration successful: $it")
                response = it
                errorMessage = null
            }.onFailure {
                Log.e("UserViewModel", "Registration failed: ${it.localizedMessage}", it)
                errorMessage = handleFetchError(it)
                Log.e("UserViewModel", "Registration failed: $errorMessage", it)

            }.also {
                isFetchingData = false
            }
        }
    }

    // Function to login a farmer
    fun loginFarmer(request: LoginRequest) {
        Log.d("UserViewModel", "loginFarmer called with request: $request")

        viewModelScope.launch {
            Log.d("UserViewModel", "Login process started")

            isFetchingData = true
            val result = repository.loginFarmer(request)
            result.onSuccess {
                Log.d("UserViewModel", "Login successful: $it")
                response = it
                errorMessage = null
            }.onFailure {
                Log.e("UserViewModel", "Login failed: ${it.localizedMessage}", it)
                errorMessage = handleFetchError(it)
            }.also {
                isFetchingData = false
            }
        }
    }

    // Function to update farmer details
    fun updateFarmer(request: UpdateRequest) {
        Log.d("UserViewModel", "updateFarmer called with request: $request")

        viewModelScope.launch {
            Log.d("UserViewModel", "Update process started")

            isFetchingData = true
            val result = repository.updateFarmer(request)
            result.onSuccess {
                Log.d("UserViewModel", "Update successful: $it")
                response = it
                errorMessage = null
            }.onFailure {
                Log.e("UserViewModel", "Update failed: ${it.localizedMessage}", it)
                errorMessage = handleFetchError(it)
            }.also {
                isFetchingData = false
            }
        }
    }

    // Function to delete a farmer
    fun deleteFarmer(request: IdRequest) {
        Log.d("UserViewModel", "deleteFarmer called with request: $request")

        viewModelScope.launch {
            Log.d("UserViewModel", "Delete process started")

            isFetchingData = true
            val result = repository.deleteFarmer(request)
            result.onSuccess {
                Log.d("UserViewModel", "Delete successful: $it")
                response = it
                errorMessage = null
            }.onFailure {
                Log.e("UserViewModel", "Delete failed: ${it.localizedMessage}", it)
                handleFetchError(it)
            }.also {
                isFetchingData = false
            }
        }
    }

    // Function to get farmer details by ID
    fun getFarmerById(farmerId: IdRequest) {
        Log.d("UserViewModel", "getFarmerById called with ID: $farmerId")
        errorMessage = null

        viewModelScope.launch {
            Log.d("UserViewModel", "Fetching farmer details")

            isFetchingData = true
            val result = repository.getUserById(farmerId)
            result.onSuccess {
                Log.d("UserViewModel", "Farmer details fetched successfully: $it")
                response = it
                errorMessage = null
            }.onFailure {
                Log.e("UserViewModel", "Fetching farmer details failed: ${it.localizedMessage}", it)
                errorMessage = handleFetchError(it)
                Log.e("UserViewModel", "Fetching farmer details failed: $errorMessage", it)
            }.also {
                isFetchingData = false
            }
        }
    }

    // A function to handle various types of errors during API calls
    fun handleFetchError(throwable: Throwable): String {
        return when (throwable) {
            // Handles case when there is no internet connection
            is UnknownHostException -> {
                "No internet connection detected. Please ensure you are connected to the internet."
            }

            // Handles case when the request times out
            is SocketTimeoutException -> {
                "The request timed out. Please check your connection and try again."
            }

            // Handles cases for unauthorized access (401) or bad request (400) errors
            is HttpException -> {
                when (throwable.code()) {
                    400 -> "Please check the data you entered."
                    401 -> "Please check your credentials."
                    404 -> "User was not found."
                    500 -> "Internal server error. Please try again later."
                    else -> "Unexpected error occurred: ${throwable.message}"
                }
            }

            // For general I/O exceptions
            is IOException -> {
                "Error during communication. Please try again later."
            }

            // Default handler for any other exceptions
            else -> {
                "An unexpected error occurred. Please try again later."
            }
        }
    }
}
