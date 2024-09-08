package com.example.farmhand.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmhand.database.entities.User
import com.example.farmhand.database.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: UserRepository) : ViewModel() {
    var _username by mutableStateOf("")
    val username: String
        get() = _username

    var _password by mutableStateOf("")
    val password: String
        get() = _password

    fun onUsernameChange(newUsername: String) {
        _username = newUsername
    }

    fun onPasswordChange(newPassword: String) {
        _password = newPassword
    }

    var passwordVisible by mutableStateOf(false)
    fun onPasswordVisibilityToggle() {
        passwordVisible = !passwordVisible
    }


    var isSignUP by mutableStateOf(false)

    val user = User(
        username = username.trim(),
        password = password.trim()
    )

    fun onAuthButtonClick() {
        if (isSignUP) {
            viewModelScope.launch {
                val errorMessages = validateInputs()
                if (errorMessages.isEmpty()) {
                    registerUser()
                } else{
                    // TODO: Show error messages
                }
            }
        } else {
            // TODO: Move to HomeScreen
        }
    }

    private suspend fun registerUser() {
        viewModelScope.launch {
            val testUser = User(
                username = _username,
                password = _password
            )
            repository.insertUser(testUser)
        }
    }

    private suspend fun validateInputs(): List<String> {
        val errorMessages = mutableListOf<String>()

        if (_username.isBlank() || _password.isBlank()) {
            errorMessages.add("Username or Password must be filled")
        }
        if (_password.length < 6) {
            errorMessages.add("Password must six characters long")
        }
        if (!_password.matches(Regex(".*[A-Z].*[a-z].*|.*[a-z].*[A-Z].*"))) {
            errorMessages.add("Password must contain at least one uppercase and lowercase letter")
        }

        return errorMessages
    }
}