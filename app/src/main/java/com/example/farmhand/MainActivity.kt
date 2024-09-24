package com.example.farmhand

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import com.example.farmhand.navigation.AuthManager
import com.example.farmhand.navigation.NavGraph
import com.example.farmhand.ui.theme.FarmHandTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FarmHandTheme {
                // Create the navigation controller
                val navController = rememberNavController()

                LaunchedEffect(Unit) {
                    if (AuthManager.isUserLoggedIn(this@MainActivity)) {
                        navController.navigate("main") {
                            // Clear the bck stack
                            popUpTo("auth") { inclusive = true }
                            Log.d("MainActivity", "User is logged in ${AuthManager.isUserLoggedIn(this@MainActivity)}")
                        }
                    } else {
                        if (navController.currentDestination?.route != "auth") {
                            navController.navigate("auth") {
                                // Clear the back stack
                                popUpTo("main") { inclusive = true }
                                Log.d("MainActivity", "User is not logged in ${AuthManager.isUserLoggedIn(this@MainActivity)}")

                            }
                        }


                    }
                }
                NavGraph(navController = navController)
            }
        }
    }
}
