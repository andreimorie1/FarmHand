package com.example.farmhand.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.farmhand.screens.AccountScreen
import com.example.farmhand.screens.AuthScreen
import com.example.farmhand.screens.HomeScreen
import com.example.farmhand.screens.PlantHealthScreen
import com.example.farmhand.screens.WeatherScreen


@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = NavItems.Home.route) {

        //auth
        composable(route = "auth") {
            AuthScreen()
        }

        //Navigation
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
            AccountScreen()
        }
    }
}
