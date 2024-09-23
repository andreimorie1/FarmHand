package com.example.farmhand.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.farmhand.components.MainAppScaffold
import com.example.farmhand.screens.AccountScreen
import com.example.farmhand.screens.AuthScreen
import com.example.farmhand.screens.HomeScreen
import com.example.farmhand.screens.PlantHealthScreen
import com.example.farmhand.screens.WeatherScreen

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
                        WeatherScreen()
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
