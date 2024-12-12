package com.example.farmhand.database.DAO

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.farmhand.database.entities.Logs
import com.example.farmhand.database.entities.Task

@Dao
interface FarmingDao {
    @Insert
    suspend fun insertTask(task: Task)

    @Insert
    suspend fun insertLog(logs: Logs)

    @Query("SELECT * FROM tasks WHERE status = 'Pending'")
    fun getPendingTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM logs")
    fun getAllLogs(): LiveData<List<Logs>>

    // Update task status
    @Query("UPDATE tasks SET status = :status WHERE id = :taskId")
    suspend fun updateTaskStatus(taskId: Int, status: String)

    @Query("SELECT * FROM tasks WHERE type = :type AND status = 'Completed'")
    fun getTasksByType(type: String): LiveData<List<Task>>

    @Query("SELECT * FROM logs WHERE type = :type")
    fun getLogsByType(type: String): LiveData<List<Logs>>

    @Query("UPDATE tasks SET status = :status, outcome = :outcome WHERE id = :taskId")
    suspend fun updateTaskWithOutcome(taskId: Int, status: String, outcome: String?)
}