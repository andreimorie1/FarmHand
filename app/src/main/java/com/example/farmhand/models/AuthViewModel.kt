package com.example.farmhand.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {
    var username by mutableStateOf("")
    var password by mutableStateOf("")

    fun onUsernameChange(newUsername: String) {
        username = newUsername
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    var passwordVisible by mutableStateOf(false)
    fun onPasswordVisibilityToggle() {
        passwordVisible = !passwordVisible
    }

    var isSignUP by mutableStateOf(false)

    fun onAuthButtonClick() {
        if (isSignUP) {
            // TODO: store to database
        } else {
            // TODO: Move to HomeScreen
        }
    }
}