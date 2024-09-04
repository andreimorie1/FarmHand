package com.example.farmhand

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.farmhand.components.MainAppScaffold
import com.example.farmhand.ui.theme.FarmHandTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FarmHandTheme {
                //AuthScreen()
                MainAppScaffold()
            }
        }
    }
}
