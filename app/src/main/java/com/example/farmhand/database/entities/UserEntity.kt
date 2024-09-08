package com.example.farmhand.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User (
    @PrimaryKey(autoGenerate = true)
    val uid: Int = 0,
    val username: String,
    val password: String
)