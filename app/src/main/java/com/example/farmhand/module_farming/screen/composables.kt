package com.example.farmhand.module_farming.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.farmhand.database.entities.Logs
import com.example.farmhand.database.entities.Task
import com.example.farmhand.module_farming.model.FarmingViewModel
import com.example.farmhand.module_weather.api.data.ThirtyDayWeather.ThirtyDayForecastResponse
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
                    "SummaryReport" -> Button(onClick = { /*insert functionality here*/ }) {
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarView(tasks: List<Task>) {
    val daysInMonth = 31  // Example for a 30-day month

    var selectedTasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }

    Column {
        // Display days of the week
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (day in 1..7) {
                Text(
                    text = when (day) {
                        1 -> "Mon"
                        2 -> "Tue"
                        3 -> "Wed"
                        4 -> "Thu"
                        5 -> "Fri"
                        6 -> "Sat"
                        else -> "Sun"
                    },
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Display calendar days
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(daysInMonth) { day ->
                DayCell(
                    day = day,
                    tasks = tasks,
                ) { tasksForDay ->
                    selectedTasks = tasksForDay
                    showDialog = true
                }
            }
        }

        // Show the Task Details Dialog
        if (showDialog) {
            TaskDetailsDialog(tasks = selectedTasks, onDismiss = { showDialog = false })
        }
    }
}

@Composable
fun LogEntryDialog(
    onDismiss: () -> Unit,
    onLogAdded: (String, String, String) -> Unit
) {
    val weatherEvents = listOf("Sunny", "Rainy", "Stormy", "Cloudy")
    val cropIssues = listOf("Pest Infestation", "Disease", "Nutrient Deficiency", "Healthy")
    val actionsTaken = listOf("Applied Pesticides", "Fertilized", "Irrigated", "No Action")

    val selectedWeather = remember { mutableStateOf("") }
    val expandedWeather = remember { mutableStateOf(false) }

    val selectedCropIssue = remember { mutableStateOf("") }
    val expandedCropIssue = remember { mutableStateOf(false) }

    val selectedAction = remember { mutableStateOf("") }
    val expandedAction = remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Entry") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Weather Dropdown
                OutlinedButton(onClick = { expandedWeather.value = !expandedWeather.value }) {
                    Text(text = if (selectedWeather.value.isEmpty()) "Select Weather Event" else selectedWeather.value)
                }
                DropdownMenu(
                    expanded = expandedWeather.value,
                    onDismissRequest = { expandedWeather.value = false }
                ) {
                    weatherEvents.forEach { event ->
                        DropdownMenuItem(
                            text = { Text(event) },
                            onClick = {
                                selectedWeather.value = event
                                expandedWeather.value = false
                            }
                        )
                    }
                }

                // Crop Issue Dropdown
                OutlinedButton(onClick = { expandedCropIssue.value = !expandedCropIssue.value }) {
                    Text(text = if (selectedCropIssue.value.isEmpty()) "Select Crop Health Issue" else selectedCropIssue.value)
                }
                DropdownMenu(
                    expanded = expandedCropIssue.value,
                    onDismissRequest = { expandedCropIssue.value = false }
                ) {
                    cropIssues.forEach { issue ->
                        DropdownMenuItem(
                            text = { Text(issue) },
                            onClick = {
                                selectedCropIssue.value = issue
                                expandedCropIssue.value = false
                            }
                        )
                    }
                }

                // Action Taken Dropdown
                OutlinedButton(onClick = { expandedAction.value = !expandedAction.value }) {
                    Text(text = if (selectedAction.value.isEmpty()) "Select Action Taken" else selectedAction.value)
                }
                DropdownMenu(
                    expanded = expandedAction.value,
                    onDismissRequest = { expandedAction.value = false }
                ) {
                    actionsTaken.forEach { action ->
                        DropdownMenuItem(
                            text = { Text(action) },
                            onClick = {
                                selectedAction.value = action
                                expandedAction.value = false
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedWeather.value.isNotEmpty() && selectedCropIssue.value.isNotEmpty() && selectedAction.value.isNotEmpty()) {
                        onLogAdded(
                            selectedWeather.value,
                            selectedCropIssue.value,
                            selectedAction.value
                        )
                    }
                },
                enabled = selectedWeather.value.isNotEmpty() && selectedCropIssue.value.isNotEmpty() && selectedAction.value.isNotEmpty()
            ) {
                Text("Add Log")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayCell(day: Int, tasks: List<Task>, onClick: (List<Task>) -> Unit) {
    // Check if the day is valid before proceeding
    if (day < 1 || day > 31) {
        return // Do nothing if day is invalid
    }

    val tasksForDay = tasks.filter {
        it.date == LocalDate.parse(
            "2024-12-${day.toString().padStart(2, '0')}",
            DateTimeFormatter.ISO_DATE
        )
    }

    val hasTasks = tasksForDay.isNotEmpty()

    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable(enabled = hasTasks) {
                if (hasTasks) {
                    // Show the tasks for the selected day in a pop-up dialog
                    onClick(tasksForDay)
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            // Display the day number
            Text(
                text = "$day",
                style = MaterialTheme.typography.bodyMedium
            )

            // Show task indicator if there are tasks
            if (hasTasks) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                        .align(Alignment.TopEnd)
                )
            }
        }
    }
}


@Composable
fun LogsList(viewModel: FarmingViewModel) {
    val logs by viewModel.logs.observeAsState(emptyList())

    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .wrapContentSize()
    ) {
        items(logs) { log ->
            LogCard(log)
        }
    }
}

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
fun TaskItem(task: Task, onTaskCompleted: (Task) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(task.task)
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { onTaskCompleted(task) },
            enabled = task.status != "Completed"
        ) {
            Text("Complete")
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
                    viewModel.generateTaskPlan(
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
fun ShowReportDialog(report: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cycle Report") },
        text = { Text(report) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

