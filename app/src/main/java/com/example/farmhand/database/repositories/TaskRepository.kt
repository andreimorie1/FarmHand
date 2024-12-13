package com.example.farmhand.database.repositories

import androidx.lifecycle.LiveData
import com.example.farmhand.database.DAO.FarmingDao
import com.example.farmhand.database.entities.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskRepository(private val farmingDao: FarmingDao) {
    // Insert a new task
    suspend fun insertTask(task: Task) {
        farmingDao.insertTask(task)
    }

    // Get all pending tasks
    fun getPendingTasks(): LiveData<List<Task>> {
        return farmingDao.getPendingTasks()
    }

    fun getAllTasks(): LiveData<List<Task>> {
        return farmingDao.getAllTasks()
    }

    fun clearAllTasks() {
        CoroutineScope(Dispatchers.IO).launch {
            farmingDao.clearAllTasks()
        }
    }

    suspend fun markTaskAsCompletedWithOutcome(taskId: Int, outcome: String?) {
        farmingDao.updateTaskWithOutcome(taskId, "Completed", outcome)
    }
}