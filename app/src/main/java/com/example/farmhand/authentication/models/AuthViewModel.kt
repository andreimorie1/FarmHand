package com.example.farmhand.authentication.models

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmhand.database.entities.User
import com.example.farmhand.database.repositories.UserRepository
import com.example.farmhand.navigation.AuthManager
import com.example.farmhand.security.PasswordHash
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: UserRepository,
) : ViewModel() {

    private val passwordHash = PasswordHash()

    //Setter
    private val _isAuthenticated = MutableStateFlow(false)
    private var _username by mutableStateOf("")
    private var _password by mutableStateOf("")
    private var _rePassword by mutableStateOf("")
    var passwordVisible by mutableStateOf(false)
    var isSignUP by mutableStateOf(false)
    var rePasswordVisible2 by mutableStateOf(false)

    fun onRePasswordChange(rePassword: String) {
        _rePassword = rePassword
    }

    fun onUsernameChange(newUsername: String) {
        _username = newUsername
    }

    fun onPasswordChange(newPassword: String) {
        _password = newPassword
    }

    fun onPasswordVisibilityToggle() {
        passwordVisible = !passwordVisible
    }

    fun onPasswordVisibilityToggle2() {
        rePasswordVisible2 = !rePasswordVisible2
    }

    //getter
    val username: String get() = _username
    val password: String get() = _password
    val rePassword: String get() = _rePassword
    val isAuthenticated : StateFlow<Boolean> = _isAuthenticated

    // Text field reset
    fun fieldReset() {
        _username = ""
        _password = ""
        _rePassword = ""
        _errorMessages.clear()
    }

    // Error messages
    private val _errorMessages = mutableStateListOf<String>()
    val errorMessages: List<String> = _errorMessages

    //Submit Button
    fun onAuthButtonClick() {
        _errorMessages.clear()
        //Sign up logic
        if (isSignUP) {
            viewModelScope.launch {
                delay(1000L)
                _errorMessages.addAll(signInValidation(_username))
                val errorMessages = signInValidation(_username)
                if (errorMessages.isEmpty()) {
                    registerUser()
                }
            }
        } else {
            viewModelScope.launch {
                _errorMessages.addAll(loginValidation())
                if (_errorMessages.isEmpty()) {
                    val user = repository.getUserByUsername(_username)
                    if (user != null) {
                        _isAuthenticated.value = true // Indicate login was successful
                        AuthManager.saveAuthState(context, _isAuthenticated.value, user.uid)
                        Log.d("AuthViewModel", "User authenticated: ${_isAuthenticated.value}")
                    } else {
                        Log.d("AuthViewModel", "User is not authenticated: ${_isAuthenticated.value}")
                        _errorMessages.add("User not found or password incorrect") // Add a message if user is not found
                    }
                }
            }
        }
    }

    // Login Logic
    private suspend fun loginValidation(): List<String> {
        val errorMessages = mutableListOf<String>()
        val toValidate = repository.getUserByUsername(_username.trim())

        if (toValidate == null) {
            errorMessages.add("User does not exist")
        }
        if (toValidate != null) {
            if (!passwordHash.verifyPassword(_password.trim(), toValidate.password)) {
                errorMessages.add("Incorrect Password")
            }
        }
        if (_username.isBlank() || _password.isBlank()) {
            errorMessages.add("Username or Password must be filled")
        }
        return errorMessages
    }

    //Register Logic
    private suspend fun registerUser() {
        val testUser = User(
            username = _username.trim(),
            password = passwordHash.hashPassword(_password.trim()),
        )
        delay(1000L)
        _errorMessages.clear()
        repository.insertUser(testUser)
        _errorMessages.add("User Registered Successfully")
    }

    // Sign Up Validations
    private suspend fun signInValidation(username: String): List<String> {
        val errorMessages = mutableListOf<String>()

        if (_username.isBlank() || _password.isBlank()) {
            errorMessages.add("Username or Password must be filled")
        }
        if (_password.length < 4) {
            errorMessages.add("Password: should be at least six characters long")
        }
        /*
        // Password: must have one uppercase and one lowercase letter
                if (!_password.matches(Regex(".*[A-Z].*[a-z].*|.*[a-z].*[A-Z].*"))) {
                    errorMessages.add("Password: at least one uppercase and lowercase letter")
                }
         */
        if (_password != _rePassword) {
            errorMessages.add("Password: do not match")
        }
        if (repository.getUserByUsername(username) != null) {
            errorMessages.add("Username: already exists")
        }
        return errorMessages
    }
}