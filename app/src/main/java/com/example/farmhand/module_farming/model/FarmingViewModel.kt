package com.example.farmhand.module_farming.model

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmhand.database.entities.Logs
import com.example.farmhand.database.entities.Task
import com.example.farmhand.database.repositories.LogRepository
import com.example.farmhand.database.repositories.TaskRepository
import com.example.farmhand.module_weather.api.data.ThirtyDayWeather.ThirtyDayForecastResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class FarmingViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val logRepository: LogRepository
) : ViewModel() {

    val tasks: LiveData<List<Task>> = taskRepository.getPendingTasks()
    val logs: LiveData<List<Logs>> = logRepository.getAllLogs()

    // Function to trigger task generation based on weather data
    @RequiresApi(Build.VERSION_CODES.O)
    fun generateTaskPlan(thirtyDayForecastData: ThirtyDayForecastResponse?, riceVariety: String) {
        viewModelScope.launch {

            val generatedTasks = generateTasks(thirtyDayForecastData, riceVariety, riceVarietyDetails)
            // Assuming you have a way to insert tasks into your repository (db)
            for (task in generatedTasks) {
                Log.d("TaskGenerator", "Generated tasks: ${generatedTasks.map { it.task }}")
                taskRepository.insertTask(task)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun completeTask(task: Task, outcome: String) {
        viewModelScope.launch {
            try {
                // Mark task as completed and update with outcome
                taskRepository.markTaskAsCompletedWithOutcome(task.id, outcome)
                Log.d("FarmingViewModel", "Task marked as completed: ${task.task}, Outcome: $outcome")

                // Insert a log entry for the completed task
                val log = Logs(
                    date = LocalDate.now(),
                    event = "Task '${task.task}' completed with outcome: $outcome"
                )
                logRepository.insertLog(log)
            } catch (e: Exception) {
                Log.e("FarmingViewModel", "Error completing task: ${e.message}")
            }
        }
    }

    val riceVarietyDetails = mapOf(
        "IR64" to mapOf("planting" to 0, "healthCheck" to 14, "fertilizing" to 10, "pestMonitoring" to "rain"),
        "NSIC Rc222" to mapOf("planting" to 0, "healthCheck" to 21, "fertilizing" to 12, "pestMonitoring" to "rain"),
        "PSB Rc18" to mapOf("planting" to 0, "healthCheck" to 14, "fertilizing" to 10, "pestMonitoring" to "rain"),
        "NSIC Rc160" to mapOf("planting" to 0, "healthCheck" to 18, "fertilizing" to 11, "pestMonitoring" to "rain"),
        "PSB Rc82" to mapOf("planting" to 0, "healthCheck" to 14, "fertilizing" to 10, "pestMonitoring" to "rain")
    )

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateTasks(
        thirtyDayForecastData: ThirtyDayForecastResponse?,
        riceVariety: String,
        riceVarietyDetails: Map<String, Map<String, Any>> // Added this parameter
    ): List<Task> {
        val tasks = mutableListOf<Task>()

        // Safety check for null or empty data
        if (thirtyDayForecastData == null || thirtyDayForecastData.list.isEmpty()) {
            Log.d("TaskGenerator", "No weather data available for task generation.")
            return tasks
        }

        Log.d("TaskGenerator", "Generating tasks for rice variety: $riceVariety")

        // Retrieve the rice variety details from the map
        val varietyDetails = riceVarietyDetails[riceVariety]

        if (varietyDetails == null) {
            Log.d("TaskGenerator", "Rice variety details not found for: $riceVariety")
            return tasks
        }

        // Find planting date
        val plantingTask = thirtyDayForecastData.list.find { day ->
            day.weather.any { it.main != "Rain" }
        }?.let { clearDay ->
            val plantingDate = Instant.ofEpochSecond(clearDay.dt.toLong())
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            Task(
                date = plantingDate,
                task = "Plant rice (${riceVariety}) on this day.",
                status = "Pending",
                type = "Planting"
            ).also {
                tasks.add(it)
                Log.d("TaskGenerator", "Added planting task for date: $plantingDate")
            }
        }

        // Ensure planting task exists before scheduling other tasks
        plantingTask?.let { planting ->
            val plantingDate = planting.date

            // 1. Post-Planting Health Monitoring (using the offset from riceVarietyDetails)
            val healthCheckOffset = varietyDetails["healthCheck"] as? Int ?: 14 // Default to 14 if not found
            tasks.add(
                Task(
                    date = plantingDate.plusDays(healthCheckOffset.toLong()),
                    task = "Take a picture of the rice plants for health monitoring.",
                    status = "Pending",
                    type = "HealthCheck"
                )
            )
            Log.d("TaskGenerator", "Added plant health monitoring task for date: ${plantingDate.plusDays(healthCheckOffset.toLong())}")

            // 2. Fertilizer Application (using the offset from riceVarietyDetails)
            val fertilizingOffset = varietyDetails["fertilizing"] as? Int ?: 10 // Default to 10 if not found
            tasks.add(
                Task(
                    date = plantingDate.plusDays(fertilizingOffset.toLong()),
                    task = "Apply fertilizer (NPK mix).",
                    status = "Pending",
                    type = "Fertilizing"
                )
            )
            Log.d("TaskGenerator", "Added fertilizing task for date: ${plantingDate.plusDays(fertilizingOffset.toLong())}")

            // 3. Pest Monitoring: Only schedule after planting if rain occurs
            val rainyDays = thirtyDayForecastData.list.filter { day ->
                day.weather.any { it.main == "Rain" || it.main == "Thunderstorm" }
            }

            rainyDays.firstOrNull()?.let { rainDay ->
                val rainDate = Instant.ofEpochSecond(rainDay.dt.toLong())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                // Schedule only if rain occurs after planting
                if (rainDate.isAfter(plantingDate)) {
                    tasks.add(
                        Task(
                            date = rainDate.plusDays(1),
                            task = "Monitor for pests and diseases after rain. Take a picture if any issues arise.",
                            status = "Pending",
                            type = "PestMonitoring"
                        )
                    )
                    Log.d("TaskGenerator", "Added pest monitoring task for date: ${rainDate.plusDays(1)}")
                } else {
                    Log.d("TaskGenerator", "Skipping pest monitoring task before planting date.")
                }

                if (rainyDays.isEmpty()) {
                    // Consider adding a task for pest monitoring based on other conditions, e.g., temperature, humidity.
                    tasks.add(
                        Task(
                            date = plantingDate.plusDays(10),  // Example: monitor on the 3rd day if no rain is expected.
                            task = "Monitor for pests and diseases even without rain.",
                            status = "Pending",
                            type = "PestMonitoring"
                        )
                    )
                }
            }

            // 4. Pest Inspection: Only after significant rain events (using trigger from variety details)
            if (rainyDays.size >= 3) {
                val lastRainyDay = rainyDays.last().let {
                    Instant.ofEpochSecond(it.dt.toLong()).atZone(ZoneId.systemDefault()).toLocalDate()
                }

                val pestInspectionOffset = varietyDetails["pestInspection"] as? Int ?: 2 // Default to 2 if not found
                val pestInspectionDate = lastRainyDay.plusDays(pestInspectionOffset.toLong())

                if (pestInspectionDate.isAfter(plantingDate)) {
                    tasks.add(
                        Task(
                            date = pestInspectionDate,
                            task = "Conduct full pest inspection due to multiple rainy days.",
                            status = "Pending",
                            type = "PestInspection"
                        )
                    )
                    Log.d("TaskGenerator", "Added pest inspection task due to heavy rain on $pestInspectionDate")
                } else {
                    Log.d("TaskGenerator", "Skipping pest inspection before planting date.")
                }
            }



            val periodicHealthCheckInterval = 7 // Example: every 7 days after the initial health check
            val periodicWeedingInterval = 7 // Weeding every 7 days after the initial planting

// Health Check Tasks every 7 days after planting
            var healthCheckDate = plantingDate.plusDays(healthCheckOffset.toLong())
            while (healthCheckDate.isBefore(plantingDate.plusDays(30))) {
                tasks.add(
                    Task(
                        date = healthCheckDate,
                        task = "Take a picture of the rice plants for health monitoring.",
                        status = "Pending",
                        type = "HealthCheck"
                    )
                )
                healthCheckDate = healthCheckDate.plusDays(periodicHealthCheckInterval.toLong())
            }

// Weeding Tasks every 7 days after planting
            var weedingDate = plantingDate.plusDays(7)
            while (weedingDate.isBefore(plantingDate.plusDays(30))) {
                tasks.add(
                    Task(
                        date = weedingDate,
                        task = "Weed the rice fields.",
                        status = "Pending",
                        type = "Weeding"
                    )
                )
                weedingDate = weedingDate.plusDays(periodicWeedingInterval.toLong())
            }

            tasks.add(
                Task(
                    date = plantingDate.plusDays(30),
                    task = "Review completed tasks and prepare for the next cycle.",
                    status = "Pending",
                    type = "SummaryReport"
                )
            )
            Log.d("TaskGenerator", "Added cycle summary task for date: ${plantingDate.plusDays(30)}")


        } ?: Log.d("TaskGenerator", "No planting task was generated, skipping other tasks.")

        // Debugging: Log the total number of tasks generated
        Log.d("TaskGenerator", "Total tasks generated: ${tasks.size}")

        return tasks
    }


}

