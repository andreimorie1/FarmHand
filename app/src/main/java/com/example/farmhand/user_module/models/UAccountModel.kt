package com.example.farmhand.user_module.models

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class uAccountModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: UserRepository,
) : ViewModel() {
    private val passwordHash = PasswordHash()
    private val _user = mutableStateOf<User?>(null)

    val user: User? get() = _user.value

    private var _username by mutableStateOf("")
    private var _password by mutableStateOf("")
    private var _rePassword by mutableStateOf("")
    private var _userId by mutableIntStateOf(-1)

    init {
        viewModelScope.launch {
            val userId = AuthManager.getUserIdLoggedIn(context) // use AuthManager to get user ID
            Log.d("uAccountModel", "User fetched: $userId")

            _user.value = repository.getUserById(userId) // Fetch user by ID using repository
            Log.d(
                "uAccountModel",
                "User fetched - username:${_user.value?.username}, ID: ${_user.value?.uid}, password: ${_user.value?.password} "
            )

            _username = _user.value?.username ?: ""
            _userId = _user.value?.uid ?: -1
        }
    }

    val userId: Int
        get() = _userId
    val username: String
        get() = _username
    val password: String
        get() = _password
    val rePassword: String
        get() = _rePassword


    private val _errorMessages = mutableStateListOf<String>()
    val errorMessages: List<String> = _errorMessages

    // Account Update Validation | Users can leave empty fields
    suspend fun AccountUpdateValidate(): List<String> {
        val errorMessages = mutableListOf<String>()

        if (username.isNotBlank()) {
            if (username.length < 4) {
                errorMessages.add("Username: should be at least four characters long")
            }
            if (repository.getUserByUsername(username) != null) {
                errorMessages.add("Username: already taken")
            }
            if (username == user?.username) {
                errorMessages.add("Username: must be new")
            }
        }

        // Validate Password
        if (password.isNotBlank()) {
            if (password.length < 4) {
                errorMessages.add("Password: should be at least four characters long")
            }
            if (password != rePassword && rePassword.isNotBlank()) {
                errorMessages.add("Password: do not match")
            }
            if (passwordHash.hashPassword(password) == user?.password) {
                errorMessages.add("Password: must be new")
            }
        }

        return errorMessages
    }


    // Update user Account
    fun onAccountUpdate() {
        _errorMessages.clear()

        viewModelScope.launch {
            var updatedUser = user?.copy()
            _errorMessages.addAll(AccountUpdateValidate())
            if (_errorMessages.isEmpty()) { // updates account if validation is successful
                if (username.isNotBlank() && username != user?.username) { // updates username only if new username is provided
                    updatedUser = updatedUser?.copy(username = username)
                }
                if (password.isNotBlank() && password == rePassword) {  // updates password only if new password is provided
                    updatedUser = updatedUser?.copy(password = passwordHash.hashPassword(password))
                }

                updatedUser?.let {
                    repository.updateUser(it)
                }
                Log.d("uAccountModel", "User updated: $updatedUser")
                _errorMessages.add("User Updated Successfully")
            }
        }
    }

    fun onLogOut() {
        AuthManager.logout(context) // Use AuthManager to log out
        Log.d("uAccountModel", "User logged out ${AuthManager.isUserLoggedIn(context)}")
    }

    //Text Field functionalities :)

    fun onPasswordChange(newPassword: String) {
        _password = newPassword
    }

    fun onRePasswordChange(newPassword: String) {
        _rePassword = newPassword
    }

    fun onUsernameChange(newUsername: String) {
        Log.d("uAccountModel", "Username changed to: $newUsername")
        _username = newUsername
    }

}
