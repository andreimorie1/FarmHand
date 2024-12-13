package com.example.farmhand.module_farming.model

import android.content.ContentValues
import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
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
        currentY = drawTextWrapped(
            "Task Completion Rate: ${"%.2f".format(report.taskCompletionRate)}%",
            10f,
            currentY
        )
        currentY = drawTextWrapped(
            "Pest Monitoring Completion: ${"%.2f".format(report.pestCompletionRate)}%",
            10f,
            currentY
        )
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
            val uri =
                contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

            uri?.let {
                contentResolver.openOutputStream(it)?.use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                    Toast.makeText(context, "PDF saved to Downloads", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            val downloadsFolder =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateFullCyclePlan(
        thirtyDayForecastData: ThirtyDayForecastResponse?,
        riceVariety: String,
    ) {
        val tasks = mutableListOf<Task>()

        // Get the total number of cycles for the selected rice variety
        val totalCycles = riceVarietyDetails[riceVariety]?.get("totalCycles") as? Int
            ?: 4  // Default to 4 cycles if not found

        for (currentCycle in 1..totalCycles) {
            // Generate the plan for each cycle
            val cycleTasks = generateCyclePlan(
                currentCycle = currentCycle,
                thirtyDayForecastData = thirtyDayForecastData,
                riceVariety = riceVariety,
                riceVarietyDetails = riceVarietyDetails
            )
            // Add the tasks for this cycle to the overall list
            tasks.addAll(cycleTasks)
        }
        viewModelScope.launch {
            tasks.forEach { task ->
                taskRepository.insertTask(task)
            }
        }
        Log.d("CyclePlanner", "Generated ${tasks.size} tasks for full cycle plan")
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun completeTask(task: Task, outcome: String) {
        viewModelScope.launch {
            try {
                // Mark task as completed and update with outcome
                taskRepository.markTaskAsCompletedWithOutcome(task.id, outcome)
                Log.d(
                    "FarmingViewModel",
                    "Task marked as completed: ${task.task}, Outcome: $outcome"
                )

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
        "IR64" to mapOf(
            "planting" to 0,
            "healthCheck" to 14,
            "fertilizing" to 10,
            "pestMonitoring" to "rain",
            "totalCycles" to 3 // 120 days divided by 30-day cycles
        ),
        "NSIC Rc222" to mapOf(
            "planting" to 0,
            "healthCheck" to 21,
            "fertilizing" to 12,
            "pestMonitoring" to "rain",
            "totalCycles" to 4 // 115 days
        ),
        "PSB Rc18" to mapOf(
            "planting" to 0,
            "healthCheck" to 14,
            "fertilizing" to 10,
            "pestMonitoring" to "rain",
            "totalCycles" to 5 // 105 days
        ),
        "NSIC Rc160" to mapOf(
            "planting" to 0,
            "healthCheck" to 18,
            "fertilizing" to 11,
            "pestMonitoring" to "rain",
            "totalCycles" to 3 // 110 days
        ),
        "PSB Rc82" to mapOf(
            "planting" to 0,
            "healthCheck" to 14,
            "fertilizing" to 10,
            "pestMonitoring" to "rain",
            "totalCycles" to 4 // 100 days
        )
    )


    @RequiresApi(Build.VERSION_CODES.O)
    fun generateCyclePlan(
        currentCycle: Int,
        thirtyDayForecastData: ThirtyDayForecastResponse?,
        riceVariety: String,
        riceVarietyDetails: Map<String, Map<String, Any>> // Rice variety-specific details
    ): List<Task> {
        val tasks = mutableListOf<Task>()

        if (thirtyDayForecastData == null || thirtyDayForecastData.list.isEmpty()) {
            Log.d("CyclePlanner", "No weather data available for cycle planning.")
            return tasks
        }

        Log.d("CyclePlanner", "Generating plan for cycle $currentCycle, rice variety: $riceVariety")

        val varietyDetails = riceVarietyDetails[riceVariety] ?: run {
            Log.d("CyclePlanner", "Rice variety details not found for: $riceVariety")
            return tasks
        }

        // Generate start date for the current cycle based on the rice variety and cycle number
        val cycleStartDate =
            LocalDate.now().plusDays((currentCycle - 1) * 30L) // Default to today for simplicity
        val cycleEndDate = cycleStartDate.plusDays(30)

        // 1. Planting Task (First Cycle Only)
        if (currentCycle == 1) {
            val plantingTask = thirtyDayForecastData.list.find { day ->
                day.weather.any { it.main != "Rain" }
            }?.let { clearDay ->
                val plantingDate = Instant.ofEpochSecond(clearDay.dt.toLong())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                // Add planting task to the list of tasks
                Task(
                    date = plantingDate,
                    task = "Plant rice (${riceVariety}) on this day.",
                    status = "Pending",
                    type = "Planting"
                ).also { tasks.add(it) }
            }

            // Ensure planting task is added even if no clear day was found
            if (plantingTask == null) {
                tasks.add(
                    Task(
                        date = cycleStartDate,  // Default to cycle start date if no clear day is found
                        task = "Plant rice (${riceVariety}) on this day.",
                        status = "Pending",
                        type = "Planting"
                    )
                )
            }
        }

        // 2. Health Monitoring Tasks (After planting task)
        val healthCheckOffset = varietyDetails["healthCheck"] as? Int ?: 14
        var healthCheckDate = cycleStartDate.plusDays(healthCheckOffset.toLong())
        val periodicHealthCheckInterval = 7 // Weekly health checks
        while (healthCheckDate.isBefore(cycleEndDate)) {
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

        // 3. Fertilizer Application Task
        val fertilizingOffset = varietyDetails["fertilizing"] as? Int ?: 10
        tasks.add(
            Task(
                date = cycleStartDate.plusDays(fertilizingOffset.toLong()),
                task = "Apply fertilizer (NPK mix).",
                status = "Pending",
                type = "Fertilizing"
            )
        )

        // 4. Pest Monitoring Tasks
        val rainyDays = thirtyDayForecastData.list.filter { day ->
            day.weather.any { it.main == "Rain" || it.main == "Thunderstorm" }
        }
        rainyDays.firstOrNull()?.let { rainDay ->
            val rainDate = Instant.ofEpochSecond(rainDay.dt.toLong())
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            if (rainDate.isAfter(cycleStartDate)) {
                tasks.add(
                    Task(
                        date = rainDate.plusDays(1),
                        task = "Monitor for pests and diseases after rain. Take a picture if any issues arise.",
                        status = "Pending",
                        type = "PestMonitoring"
                    )
                )
            }
        }

        // 5. Weeding Tasks (After planting and fertilizing tasks)
        val periodicWeedingInterval = 7 // Weekly weeding
        var weedingDate = cycleStartDate.plusDays(12)
        while (weedingDate.isBefore(cycleEndDate)) {
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
//dsa
        // 6. Prepare for the Next Cycle
        tasks.add(
            Task(
                date = cycleEndDate,
                task = "Review completed cycle and prepare for the next rice stage.",
                status = "Pending",
                type = "SummaryReport"
            )
        )

        Log.d("CyclePlanner", "Generated ${tasks.size} tasks for cycle $currentCycle")
        return tasks
    }


    fun generateSummaryReport(tasks: List<Task>, weatherLogs: List<WeatherLog>): SummaryReport {
        val completedTasks = tasks.filter { it.status == "Completed" }
        val pendingTasks = tasks.filter { it.status != "Completed" }

        val rainyDays = weatherLogs.count { it.weatherMain == "Rain" }
        val dryDays = weatherLogs.size - rainyDays

        val healthCheckIssues =
            tasks.filter { it.type == "HealthCheck" || it.type == "PestInspection" || it.type == "Weeding" && it.outcome == "Pests Detected" || it.outcome == "Diseased" }

        val completionRate = if (tasks.isNotEmpty()) {
            (completedTasks.size.toDouble() / tasks.size.toDouble()) * 100
        } else 0.0

        val pestMonitoringTasks =
            tasks.filter { it.type == "PestMonitoring" || it.type == "HealthCheck" || it.type == "PestInspection" || it.type == "Weeding" }
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
            recommendations = generateRecommendations(
                pendingTasks,
                healthCheckIssues,
                completedTasks,
                weatherLogs
            )
        )
    }


    fun generateRecommendations(
        pendingTasks: List<Task>,
        healthIssues: List<Task>,
        completedTasks: List<Task>,
        weatherLogs: List<WeatherLog>
    ): List<String> {
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

