package com.example.farmhand.module_farming.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.farmhand.database.entities.Logs
import com.example.farmhand.database.entities.Task
import com.example.farmhand.module_farming.model.FarmingViewModel
import com.example.farmhand.module_weather.api.data.ThirtyDayWeather.ThirtyDayForecastResponse

/*
composables

SectionHeader
TaskList
CalendarView
TaskCard
DayCell
TaskProgressButton
LogsList
LogCard
TaskListScreen
TaskItem

 */

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCard(task: Task, onCompleteTask: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOutcome by remember { mutableStateOf("") }

    // Options for the dropdown based on task type (example: "Healthy", "Pests Detected")
    val outcomeOptions = when (task.type) {
        "Fertilizing", "HealthCheck" -> listOf("Healthy", "Pests Detected", "Diseased")
        "PestInspection" -> listOf("Healthy", "Pests Detected")
        else -> {
            listOf("Healthy", "Pests Detected", "Diseased")
        }
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = task.task,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Scheduled on: ${task.date}",
                style = MaterialTheme.typography.bodySmall
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Status: ${task.status}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )


                when (task.type) {
                    "Planting", "Fertilizing", "Weeding" -> Button(onClick = { onCompleteTask("Okay") }) {
                        Text("Mark as Done")
                    }
                    "SummaryReport" -> Button(onClick = { onCompleteTask("SummaryReport") }) {
                        Text("See results")
                    }
                    else -> {
                        ExposedDropdownMenuBox(
                            modifier = Modifier
                                .width(180.dp)
                                .wrapContentHeight(),
                            expanded = expanded,
                            onExpandedChange = { expanded = it }) {
                            TextField(
                                value = selectedOutcome,
                                onValueChange = { selectedOutcome = it },
                                label = { Text("Select Outcome") },
                                readOnly = true,
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Dropdown Icon"
                                    )
                                },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                outcomeOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(text = option) },
                                        onClick = {
                                            selectedOutcome = option
                                            expanded = false
                                            onCompleteTask(selectedOutcome)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}


/*
@Composable
fun TaskCard(task: Task) {
    // Card for each task, displaying the task details
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = task.task,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Scheduled on: ${task.date}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Status: ${task.status}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
 */

@Composable
fun LogCard(log: Logs) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = log.event,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Logged on: ${log.date}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun TaskDetailsDialog(tasks: List<Task>, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tasks for the day") },
        text = {
            Column {
                tasks.forEach { task ->
                    Text(
                        text = "- ${task.task}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PlanCreationDialog(
    weatherdata: ThirtyDayForecastResponse?,
    viewModel: FarmingViewModel,
    onDismiss: () -> Unit,
    onPlanCreated: () -> Unit
) {
    val riceVarieties =
        listOf("IR64", "NSIC Rc222", "PSB Rc18", "NSIC Rc160", "PSB Rc82") // Example varieties
    var selectedVariety by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Create Farming Plan") },
        text = {
            Column {
                OutlinedButton(onClick = { expanded = !expanded }) {
                    Text(if (selectedVariety.isNotEmpty()) selectedVariety else "Select Variety")
                }

                DropdownMenu(
                    expanded = expanded, // Use the state variable
                    onDismissRequest = { expanded = false }
                ) {
                    riceVarieties.forEach { variety ->
                        DropdownMenuItem(
                            onClick = {
                                selectedVariety = variety
                                expanded = false // Close the menu after selection
                            },
                            text = { Text(variety) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.generateFullCyclePlan(
                        thirtyDayForecastData = weatherdata,
                        riceVariety = selectedVariety
                    )
                    onPlanCreated() // Close dialog and refresh tasks
                },
                enabled = selectedVariety.isNotEmpty()
            ) {
                Text(text = "Generate Plan")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        }
    )
}

@Composable
fun SummaryReportDialog(
    report: FarmingViewModel.SummaryReport,
    onDismiss: () -> Unit,
    onDownloadPdf: () -> Unit,
    ) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),

            ) {
                Text("Summary Report", style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.height(8.dp))

                Text("Overview", style = MaterialTheme.typography.bodyMedium)
                Text("Total Tasks: ${report.totalTasks}", style = MaterialTheme.typography.bodySmall)
                Text("Completed: ${report.completedTasks}", style = MaterialTheme.typography.bodySmall)
                Text("Pending: ${report.pendingTasks}", style = MaterialTheme.typography.bodySmall)
                Text("Task Completion Rate: ${"%.2f".format(report.taskCompletionRate)}%", style = MaterialTheme.typography.bodySmall)
                Text("Pest Completion Rate: ${"%.2f".format(report.pestCompletionRate)}%", style = MaterialTheme.typography.bodySmall)


                Spacer(modifier = Modifier.height(8.dp))

                Text("Weather Summary", style = MaterialTheme.typography.bodyMedium)
                Text("Rainy Days: ${report.rainyDays}", style = MaterialTheme.typography.bodySmall)
                Text("Dry Days: ${report.dryDays}", style = MaterialTheme.typography.bodySmall)

                Spacer(modifier = Modifier.height(8.dp))

                Text("Health Insights", style = MaterialTheme.typography.bodyMedium)
                Text("Issues Detected: ${report.healthIssuesCount}", style = MaterialTheme.typography.bodySmall)

                Spacer(modifier = Modifier.height(8.dp))

                Text("Outcome", style = MaterialTheme.typography.bodyMedium)
                Text(report.cycleSuccess, style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(4.dp))
                Text(report.weatherImpact, style = MaterialTheme.typography.bodySmall)

                Spacer(modifier = Modifier.height(8.dp))

                Text("Recommendations")
                report.recommendations.forEach {
                    Text("- $it", style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = onDownloadPdf) {
                        Text("Download PDF")
                    }
                    Button(onClick = onDismiss) {
                        Text("Close")
                    }
                }
            }
        }
    }
}



