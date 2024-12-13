package com.example.farmhand.database.DAO

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.farmhand.database.entities.Logs
import com.example.farmhand.database.entities.Task
import com.example.farmhand.database.entities.WeatherLog

@Dao
interface FarmingDao {
    @Insert
    suspend fun insertTask(task: Task)

    @Insert
    suspend fun insertLog(logs: Logs)

    @Query("SELECT * FROM tasks WHERE status = 'Pending'")
    fun getPendingTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): LiveData<List<Task>>

    @Query("DELETE FROM tasks")
    suspend fun clearAllTasks()

    @Query("SELECT * FROM logs")
    fun getAllLogs(): LiveData<List<Logs>>

    // Update task status
    @Query("UPDATE tasks SET status = :status WHERE id = :taskId")
    suspend fun updateTaskStatus(taskId: Int, status: String)

    @Query("SELECT * FROM tasks WHERE type = :type AND status = 'Completed'")
    fun getTasksByType(type: String): LiveData<List<Task>>

    @Query("DELETE FROM logs")
    fun clearAllLogs()

    @Query("SELECT * FROM logs WHERE type = :type")
    fun getLogsByType(type: String): LiveData<List<Logs>>

    @Query("UPDATE tasks SET status = :status, outcome = :outcome WHERE id = :taskId")
    suspend fun updateTaskWithOutcome(taskId: Int, status: String, outcome: String?)

    @Insert
    suspend fun insertWeatherLog(weatherLog: List<WeatherLog>)

    @Query("SELECT * FROM weather_log WHERE date >= :startDate AND date <= :endDate")
    suspend fun getWeatherLogsBetweenDates(startDate: Long, endDate: Long): List<WeatherLog>

    @Query("DELETE FROM weather_log")
    suspend fun deleteOldWeatherLogs()
}