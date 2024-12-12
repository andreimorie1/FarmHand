package com.example.farmhand.database.repositories

import androidx.lifecycle.LiveData
import com.example.farmhand.database.DAO.FarmingDao
import com.example.farmhand.database.entities.Logs

class LogRepository(private val farmingDao: FarmingDao) {
    // Insert a new log
    suspend fun insertLog(logs: Logs) {
        farmingDao.insertLog(logs)
    }
    // Get all logs
    fun getAllLogs(): LiveData<List<Logs>> {
        return farmingDao.getAllLogs()
    }
}