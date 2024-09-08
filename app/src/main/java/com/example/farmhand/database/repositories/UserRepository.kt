package com.example.farmhand.database.repositories


import com.example.farmhand.database.database.AppDatabase
import com.example.farmhand.database.entities.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Repository class for managing User data operations
class UserRepository(private val database: AppDatabase) {

    // Function to insert a user into the database
    suspend fun insertUser(user: User) {
        // Run the database operation on the IO thread
        withContext(Dispatchers.IO) {
            database.userDao.insertUser(user)
        }
    }

    // Function to update a user in the database
    suspend fun updateUser(user: User) {
        withContext(Dispatchers.IO) {
            database.userDao.updateUser(user)
        }
    }

    // Function to delete a user from the database
    suspend fun deleteUser(user: User) {
        withContext(Dispatchers.IO) {
            database.userDao.deleteUser(user)
        }
    }

    // Function to get user information by user ID
    suspend fun getAccountInfo(uid: Int): User? {
        return withContext(Dispatchers.IO) {
            database.userDao.getAccountInfo(uid)
        }
    }

    // Function to get user information by username
    suspend fun getUserByUsername(username: String): User? {
        return withContext(Dispatchers.IO) {
            database.userDao.getUserByUsername(username)
        }
    }
}