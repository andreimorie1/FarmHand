package com.example.farmhand

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.farmhand.Screens.AuthScreen
import com.example.farmhand.authentication.authModels.AuthViewModel
import com.example.farmhand.ui.theme.FarmHandTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FarmHandTheme {
                val authViewModel = AuthViewModel()
                AuthScreen(authViewModel)
            }
        }
    }
}
