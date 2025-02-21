package com.example.farmhand.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.farmhand.navigation.components.MainAppScaffold
import com.example.farmhand.module_user.screens.AccountScreen
import com.example.farmhand.authentication.screen.AuthScreen
import com.example.farmhand.module_Reco.model.OpenAiViewModel
import com.example.farmhand.module_farming.model.FarmingViewModel
import com.example.farmhand.module_farming.screen.farmingScreen
import com.example.farmhand.module_health.models.KindwiseViewModel
import com.example.farmhand.module_health.screens.PlantHealthScreen
import com.example.farmhand.module_user.models.UserViewModel
import com.example.farmhand.module_weather.models.WeatherViewModel
import com.example.farmhand.module_weather.screens.WeatherScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(navController: NavHostController) {
    val weatherViewModel: WeatherViewModel = hiltViewModel()
    val KindwiseViewModel: KindwiseViewModel = hiltViewModel()
    val OpenAiViewModel: OpenAiViewModel = hiltViewModel()
    val userViewModel: UserViewModel = hiltViewModel()
    val farmingViewModel: FarmingViewModel = hiltViewModel()
    NavHost(navController = navController, startDestination = "main") {
        // Auth Screen
        composable(route = "auth") {
            AuthScreen(navController, userViewModel = userViewModel)
        }

        // Main Application Scaffold with Bottom Navigation
        composable(route = "main") {

            MainAppScaffold(
                //navController,
            ) { innerNavController ->
                NavHost(
                    navController = innerNavController,
                    startDestination = NavItems.Weather.route // Default tab
                ) {
                    composable(route = NavItems.Weather.route) {
                        WeatherScreen(viewModel = weatherViewModel)
                    }
                    composable(route = NavItems.PlantHealth.route) {
                        PlantHealthScreen(weatherViewModel = weatherViewModel, KindwiseViewModel = KindwiseViewModel, OpenAiViewModel = OpenAiViewModel)
                    }
                    composable(route = NavItems.Account.route) {
                        AccountScreen(navController, userViewModel)
                    }
                    composable(route = NavItems.Farming.route) {
                        farmingScreen(viewModel = farmingViewModel, weatherdata = weatherViewModel.thirtyDayForecastData!!)
                    }
                }
            }
        }
    }
}
