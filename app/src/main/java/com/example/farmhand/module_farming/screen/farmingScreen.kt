package com.example.farmhand.module_farming.screen

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import com.example.farmhand.database.entities.Task
import com.example.farmhand.database.repositories.TaskRepository
import com.example.farmhand.module_farming.model.FarmingViewModel
import com.example.farmhand.module_weather.api.data.ThirtyDayWeather.ThirtyDayForecastResponse

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun farmingScreen(viewModel: FarmingViewModel, weatherdata: ThirtyDayForecastResponse) {
    val context = LocalContext.current
    val tasks = viewModel.tasks.observeAsState(emptyList())
    val logs = viewModel.logs.observeAsState(emptyList())

    val AllTasks = viewModel.allTasks.observeAsState(emptyList())

    var showDialog by remember { mutableStateOf(false) }
    var reportDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Plan")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .padding(start = 3.dp, end = 3.dp, top = 10.dp)
                .verticalScroll(rememberScrollState()) // Ensures content is scrollable

        ) {
            Text(
                text = "Farmhand Assistant",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(paddingValues)
            )
            Text(
                text = "Card View",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding()
            )
            SectionHeader("Upcoming tasks")
// Sort tasks by date before displaying them
            tasks.value.sortedBy { it.date }.forEach { task ->
                TaskCard(
                    task,
                    onCompleteTask = { outcome ->
                        // Pass the selected outcome to completeTask method
                        viewModel.completeTask(task, outcome)
                        if (outcome == "SummaryReport") {
                            reportDialog = true
                        }
                    }
                )
            }

            //TaskList(viewModel = viewModel)

            SectionHeader("Task log")
            logs.value.forEach { log ->
                LogCard(log)
            }

            Button(
                modifier = Modifier.padding(16.dp),
                onClick = {
                viewModel.resetCycleData()
            }) {
                Text("Reset Plan")
            }
        }
        if (reportDialog) {
            SummaryReportDialog(
                report = viewModel.generateSummaryReport(
                    tasks = AllTasks.value,
                    weatherLogs = viewModel.generateWeatherLogs(weatherdata)
                ),
                onDismiss = { reportDialog = false
                    viewModel.resetCycleData() },
                onDownloadPdf = {
                    viewModel.generatePdf(
                        context = context,
                        report = viewModel.generateSummaryReport(
                            tasks = tasks.value,
                            weatherLogs = viewModel.generateWeatherLogs(weatherdata)
                        )
                    )
                }
            )
        }
        // Show the Plan Creation Dialog
        if (showDialog) {
            PlanCreationDialog(
                viewModel = viewModel,
                onDismiss = { showDialog = false },
                onPlanCreated = { showDialog = false },
                weatherdata = weatherdata
            )
        }
    }
}

