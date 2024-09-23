package com.example.farmhand.models

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.farmhand.navigation.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class uAccountModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private var _username by mutableStateOf("")
    private var _password by mutableStateOf("")
    private var _userID by mutableIntStateOf(-1)

    val userID: Int
        get() = _userID
    val username: String
        get() = _username
    val password: String
        get() = _password

    // Update username
    fun onUsernameChange(newUsername: String) {
        Log.d("uAccountModel", "Username changed to: $newUsername")
        _username = newUsername
    }

    // Update user Account
    fun onAccountUpdate(
        username: String,
        password: String,
    ) {
        //TODO: UPDATE USER ACCOUNT
    }

    fun onLogOut() {
        AuthManager.logout(context) // Use AuthManager to log out
        Log.d("uAccountModel", "User logged out ${AuthManager.isUserLoggedIn(context)}")
    }

    // Update password
    fun onPasswordChange(newPassword: String) {
        _password = newPassword
    }
}
