package com.example.farmhand.database.repositories

import androidx.lifecycle.LiveData
import com.example.farmhand.database.DAO.FarmingDao
import com.example.farmhand.database.entities.Task

class TaskRepository(private val farmingDao: FarmingDao) {
    // Insert a new task
    suspend fun insertTask(task: Task) {
        farmingDao.insertTask(task)
    }

    // Get all pending tasks
    fun getPendingTasks(): LiveData<List<Task>> {
        return farmingDao.getPendingTasks()
    }

    // Mark task as completed
    suspend fun markTaskAsCompleted(taskId: Int) {
        farmingDao.updateTaskStatus(taskId, "Completed")
    }

    fun getTasksByType(type: String): LiveData<List<Task>> {
        return farmingDao.getTasksByType(type)
    }

    suspend fun markTaskAsCompletedWithOutcome(taskId: Int, outcome: String?) {
        farmingDao.updateTaskWithOutcome(taskId, "Completed", outcome)
    }
}