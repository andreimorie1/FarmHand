package com.example.farmhand.models

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.farmhand.navigation.NavGraph


@Composable
fun AppEntryPoint() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()

    // Collecting state from ViewModel
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

    LaunchedEffect(Unit) {
        Log.d("AppEntryPoint", "isAuthenticated: $isAuthenticated")
    }

    LaunchedEffect(isAuthenticated) {
        Log.d("AppEntryPoint", "LaunchedEffect triggered with auth state: $isAuthenticated")
        if (isAuthenticated) {
            Log.d("AppEntryPoint", "Navigating to home")
            navController.navigate("scaffold") {
                popUpTo("auth") {
                    inclusive = true
                }
            }
        } else {
            Log.d("AppEntryPoint", "Navigating to login")
            navController.navigate("auth") {
                popUpTo("scaffold") {
                    inclusive = true
                }
            }
        }
    }

    NavGraph(navController = navController)
}
