package com.example.farmhand.database.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.farmhand.database.entities.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    @Update
    fun updateUser(user: User)

    @Delete
    fun deleteUser(user: User)

    @Query("Select * FROM user WHERE uid = :uid")
    fun getAccountById(uid: Int): User?

    @Query("SELECT * FROM user WHERE username = :username")
    fun getUserByUsername(username: String): User?
}

