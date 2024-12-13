package com.example.farmhand.database.repositories

import com.example.farmhand.database.DAO.FarmingDao
import com.example.farmhand.database.entities.WeatherLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class WeatherLogRepository @Inject constructor(private val weatherLogDao: FarmingDao) {

    // Insert a list of weather logs - no need to call coroutine manually, handles on background thread
    suspend fun insertWeatherLogs(weatherLogs: List<WeatherLog>) {
        CoroutineScope(Dispatchers.IO).launch {
            weatherLogDao.insertWeatherLog(weatherLogs)
        }
    }

    // Get weather logs within a specific date range - returns LiveData, no need for coroutines on the calling side
    suspend fun getWeatherLogsBetweenDates(startDate: Long, endDate: Long): List<WeatherLog> {
        return weatherLogDao.getWeatherLogsBetweenDates(startDate, endDate)
    }

    // Delete weather logs older than a certain date - handles background task automatically
    suspend fun deleteOldWeatherLogs() {
        CoroutineScope(Dispatchers.IO).launch {
            weatherLogDao.deleteOldWeatherLogs()
        }
    }
}

