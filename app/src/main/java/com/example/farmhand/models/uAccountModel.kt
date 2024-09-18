package com.example.farmhand.models

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmhand.data.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class uAccountModel @Inject constructor(userDataRepository: UserDataRepository) : ViewModel() {

    private var _username by mutableStateOf("")
    private var _password by mutableStateOf("")
    private var _userID by mutableIntStateOf(-1)

    // Collect user data from repository
    init {
        viewModelScope.launch {
            userDataRepository.currentUser.collect { user ->
                Log.d("uAccountModel", "User collected: ${user?.username ?: "null"}")
                _username = user?.username ?: "Did not work"
                _userID = user?.uid ?: 0
                Log.d("uAccountModel", "Username updated: $_username, UserID updated: $_userID")
            }
        }
    }

    // Getter UserID
    val userID: Int
        get() = _userID

    // Getter username
    val username: String
        get() = _username

    // Getter password
    val password: String
        get() = _password

    // Update username
    fun onUsernameChange(newUsername: String) {
        Log.d("uAccountModel", "Username changed to: $newUsername")
        _username = newUsername
    }

    // Update password
    fun onPasswordChange(newPassword: String) {
        _password = newPassword
    }
}
