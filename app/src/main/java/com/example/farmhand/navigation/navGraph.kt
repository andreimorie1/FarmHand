package com.example.farmhand.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.farmhand.navigation.components.MainAppScaffold
import com.example.farmhand.user_module.screens.AccountScreen
import com.example.farmhand.authentication.screen.AuthScreen
import com.example.farmhand.screens.HomeScreen
import com.example.farmhand.screens.PlantHealthScreen
import com.example.farmhand.weather.models.WeatherViewModel
import com.example.farmhand.weather.screens.WeatherScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "auth") {
        // Auth Screen
        composable(route = "auth") {
            AuthScreen(navController)
        }

        // Main Application Scaffold with Bottom Navigation
        composable(route = "main") {
            MainAppScaffold(navController) { innerNavController ->
                NavHost(
                    navController = innerNavController,
                    startDestination = NavItems.Home.route // Default tab
                ) {
                    composable(route = NavItems.Home.route) {
                        HomeScreen()
                    }
                    composable(route = NavItems.Weather.route) {
                        val weatherViewModel: WeatherViewModel = hiltViewModel()
                        WeatherScreen(viewModel = weatherViewModel, LocalContext.current)
                    }
                    composable(route = NavItems.PlantHealth.route) {
                        PlantHealthScreen()
                    }
                    composable(route = NavItems.Account.route) {
                        AccountScreen(navController)
                    }
                }
            }
        }
    }
}
