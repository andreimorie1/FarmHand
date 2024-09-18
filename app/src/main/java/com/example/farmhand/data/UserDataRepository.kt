package com.example.farmhand.data

import android.util.Log
import com.example.farmhand.database.entities.User
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class UserDataRepository @Inject constructor(){
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: MutableStateFlow<User?> get() = _currentUser

    fun setCurrentUser(user: User) {
        _currentUser.value = user
    }
}