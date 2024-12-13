package com.example.farmhand.module_farming.model

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmhand.database.entities.Logs
import com.example.farmhand.database.entities.Task
import com.example.farmhand.database.entities.WeatherLog
import com.example.farmhand.database.repositories.LogRepository
import com.example.farmhand.database.repositories.TaskRepository
import com.example.farmhand.database.repositories.WeatherLogRepository
import com.example.farmhand.module_weather.api.data.ThirtyDayWeather.ThirtyDayForecastResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class FarmingViewModel @Inject constructor(
    private val weatherLogRepository: WeatherLogRepository,
    private val taskRepository: TaskRepository,
    private val logRepository: LogRepository
) : ViewModel() {

    val tasks: LiveData<List<Task>> = taskRepository.getPendingTasks()
    val logs: LiveData<List<Logs>> = logRepository.getAllLogs()

    val allTasks: LiveData<List<Task>> = taskRepository.getAllTasks()

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateWeatherLogs(weatherData: ThirtyDayForecastResponse): List<WeatherLog> {
        return weatherData.list.flatMap { weatherLog ->
            weatherLog.weather.map { weather ->
                WeatherLog(
                    date = Instant.ofEpochSecond(weatherLog.dt.toLong())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate(),
                    weatherMain = weather.main
                )
            }
        }
    }

    fun resetCycleData() {
        CoroutineScope(Dispatchers.IO).launch {
            taskRepository.clearAllTasks()
            logRepository.clearAllLogs()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun generatePdf(context: Context, report: SummaryReport) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
        val page = pdfDocument.startPage(pageInfo)

        val canvas = page.canvas
        val paint = Paint()
        paint.textSize = 14f
        val textWidth = pageInfo.pageWidth - 20f // Keep some margin
        val lineSpacing = 20f // Vertical line spacing

        fun drawTextWrapped(text: String, x: Float, y: Float): Float {
            val words = text.split(" ")
            var line = ""
            var currentY = y

            for (word in words) {
                val testLine = "$line $word"
                if (paint.measureText(testLine) <= textWidth) {
                    line = testLine
                } else {
                    canvas.drawText(line, x, currentY, paint)
                    currentY += lineSpacing  // Move to the next line
                    line = word
                }
            }
            canvas.drawText(line, x, currentY, paint)  // Draw the last line
            return currentY + lineSpacing // Return the new vertical position
        }

        var currentY = 25f // Starting Y position

        // Report Details
        currentY = drawTextWrapped("Summary Report", 10f, currentY)
        currentY = drawTextWrapped("Total Tasks: ${report.totalTasks}", 10f, currentY)
        currentY = drawTextWrapped("Completed: ${report.completedTasks}", 10f, currentY)
        currentY = drawTextWrapped("Pending: ${report.pendingTasks}", 10f, currentY)
        currentY = drawTextWrapped("Rainy Days: ${report.rainyDays}", 10f, currentY)
        currentY = drawTextWrapped("Dry Days: ${report.dryDays}", 10f, currentY)
        currentY = drawTextWrapped("Health Issues: ${report.healthIssuesCount}", 10f, currentY)

        // Additional Insights
        currentY = drawTextWrapped("Task Completion Rate: ${"%.2f".format(report.taskCompletionRate)}%", 10f, currentY)
        currentY = drawTextWrapped("Pest Monitoring Completion: ${"%.2f".format(report.pestCompletionRate)}%", 10f, currentY)
        currentY = drawTextWrapped("Weather Impact: ${report.weatherImpact}", 10f, currentY)

        // Cycle Success Evaluation
        currentY = drawTextWrapped("Cycle Success: ${report.cycleSuccess}", 10f, currentY)

        // Recommendations
        currentY = drawTextWrapped("Recommendations:", 10f, currentY)
        report.recommendations.forEachIndexed { index, recommendation ->
            currentY = drawTextWrapped("- $recommendation", 10f, currentY)
        }

        pdfDocument.finishPage(page)

        // Handle file saving (same as previous code for handling Android versions)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "summary_report.pdf")
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val contentResolver = context.contentResolver
            val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

            uri?.let {
                contentResolver.openOutputStream(it)?.use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                    Toast.makeText(context, "PDF saved to Downloads", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            val downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsFolder, "summary_report.pdf")

            if (!downloadsFolder.exists()) {
                downloadsFolder.mkdirs()
            }

            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(context, "PDF saved to ${file.absolutePath}", Toast.LENGTH_LONG).show()
        }

        pdfDocument.close()
    }



    suspend fun getWeatherLogs(startDate: Long, endDate: Long): List<WeatherLog> {
        return weatherLogRepository.getWeatherLogsBetweenDates(startDate, endDate)
    }

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
                    date = plantingDate.plusDays(32),
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

    fun generateSummaryReport(tasks: List<Task>, weatherLogs: List<WeatherLog>): SummaryReport {
        val completedTasks = tasks.filter { it.status == "Completed" }
        val pendingTasks = tasks.filter { it.status != "Completed" }

        val rainyDays = weatherLogs.count { it.weatherMain == "Rain" }
        val dryDays = weatherLogs.size - rainyDays

        val healthCheckIssues = tasks.filter { it.type == "HealthCheck" || it.type == "PestInspection" || it.type == "Weeding" && it.outcome == "Pests Detected" || it.outcome == "Diseased" }

        val completionRate = if (tasks.isNotEmpty()) {
            (completedTasks.size.toDouble() / tasks.size.toDouble()) * 100
        } else 0.0

        val pestMonitoringTasks = tasks.filter { it.type == "PestMonitoring" || it.type == "HealthCheck" || it.type == "PestInspection" || it.type == "Weeding" }
        val completedPestTasks = pestMonitoringTasks.filter { it.status == "Completed" }
        val pestCompletionRate = if (pestMonitoringTasks.isNotEmpty()) {
            (completedPestTasks.size.toDouble() / pestMonitoringTasks.size.toDouble()) * 100
        } else 0.0

        val weatherImpactAnalysis = if (rainyDays > 0) {
            "Rainy days impacted task completion, especially for pest monitoring."
        } else {
            "Dry days had a positive impact on task completion."
        }

        // Success evaluation based on task completion and weather impact
        val cycleSuccess = if (completionRate >= 80 && pestCompletionRate >= 75) {
            "Cycle was successful! Great job on completing most tasks and managing pests well."
        } else if (completionRate < 50) {
            "Cycle needs improvement. Consider reviewing pending tasks and pest management strategies."
        } else {
            "Cycle was somewhat successful. Keep an eye on improving task completion and pest control."
        }

        return SummaryReport(
            totalTasks = tasks.size,
            completedTasks = completedTasks.size,
            pendingTasks = pendingTasks.size,
            rainyDays = rainyDays,
            dryDays = dryDays,
            healthIssuesCount = healthCheckIssues.size,
            taskCompletionRate = completionRate,
            pestCompletionRate = pestCompletionRate,
            weatherImpact = weatherImpactAnalysis,
            cycleSuccess = cycleSuccess,  // Added success evaluation
            recommendations = generateRecommendations(pendingTasks, healthCheckIssues, completedTasks, weatherLogs)
        )
    }


    fun generateRecommendations(pendingTasks: List<Task>, healthIssues: List<Task>, completedTasks: List<Task>, weatherLogs: List<WeatherLog>): List<String> {
        val recommendations = mutableListOf<String>()

        // Pending Tasks
        if (pendingTasks.isNotEmpty()) {
            recommendations.add("Ensure all pending tasks are completed to avoid delays in the next cycle. Pending tasks could impact future yields.")
        }

        // Health Issues
        if (healthIssues.isNotEmpty()) {
            recommendations.add("Address the health issues identified during inspections promptly. Delayed actions could lead to further crop stress and reduced yields.")
        }

        // Pest and Disease Monitoring
        val pestTasks = completedTasks.filter { it.type == "PestMonitoring" }
        val uncompletedPestTasks = pendingTasks.filter { it.type == "PestMonitoring" }
        if (pestTasks.isEmpty() || uncompletedPestTasks.isNotEmpty()) {
            recommendations.add("Consider increasing frequency of pest and disease monitoring to avoid infestations, especially if tasks were not completed.")
        }

        // Weather Impact Reflection
        val rainyDays = weatherLogs.count { it.weatherMain == "Rain" }
        if (rainyDays > 5) {  // Assuming 5 rainy days as a threshold for major impact
            recommendations.add("Excessive rain during the cycle might have hindered some tasks, especially those requiring dry conditions. Consider adjusting your task timing to avoid weather disruptions in the future.")
        }

        // Review of Task Completion Rate
        val completionRate = if (completedTasks.isNotEmpty()) {
            (completedTasks.size.toDouble() / (completedTasks.size + pendingTasks.size)) * 100
        } else 0.0
        if (completionRate < 75) {
            recommendations.add("Task completion rate is below 75%. Review the tasks that were left incomplete and strategize on ways to improve task follow-up and time management.")
        }

        return recommendations
    }


    data class SummaryReport(
        val totalTasks: Int,
        val completedTasks: Int,
        val pendingTasks: Int,
        val rainyDays: Int,
        val dryDays: Int,
        val healthIssuesCount: Int,
        val taskCompletionRate: Double,
        val pestCompletionRate: Double,
        val weatherImpact: String,
        val cycleSuccess: String,
        val recommendations: List<String>
    )

}

