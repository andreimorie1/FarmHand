package com.example.farmhand.database.repositories

import androidx.lifecycle.LiveData
import com.example.farmhand.database.DAO.FarmingDao
import com.example.farmhand.database.entities.Logs
import com.example.farmhand.database.entities.WeatherLog
import com.example.farmhand.module_weather.api.data.ThirtyDayWeather.ThirtyDayForecastResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class LogRepository(private val farmingDao: FarmingDao) {
    // Insert a new log
    suspend fun insertLog(logs: Logs) {
        farmingDao.insertLog(logs)
    }

    fun clearAllLogs() {
        CoroutineScope(Dispatchers.IO).launch {
            farmingDao.clearAllLogs()
        }
    }
    // Get all logs
    fun getAllLogs(): LiveData<List<Logs>> {
        return farmingDao.getAllLogs()
    }
}