package com.example.farmhand.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class User (
    @PrimaryKey(autoGenerate = true)
    val uid: Int = 0,
    val username: String,
    val password: String
)

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: LocalDate,
    val task: String,
    val status: String, // "Pending", "Completed"
    val type: String, // E.g., "PestMonitoring", "HealthCheck", etc.
    val outcome: String? = null // Store dropdown outcome for insights
)

@Entity(tableName = "logs")
data class Logs(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: LocalDate,
    val event: String, // Events like weather, pest observations,
    val type: String? = null // Optional, categorize logs
)

@Entity(tableName = "weather_log")
data class WeatherLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "date") val date: LocalDate, // Store the date as a timestamp
    @ColumnInfo(name = "weather_main") val weatherMain: String // The main weather condition like "Rain", "Clear", etc.
)


