package com.example.farmhand

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.farmhand.screens.AuthScreen
import com.example.farmhand.ui.theme.FarmHandTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
/*
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            name = "userdata"
        ).build()
    }
    private val viewModel by viewModels<AuthViewModel> (
        factoryProducer = {
            object : ViewModelProvider.Factory{
                override fun <T: ViewModel> create(modelClass: Class<T>): T {
                    return AuthViewModel(UserRepository(db)) as T
                }
            }
        }
    )
 */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FarmHandTheme {
                AuthScreen()
                //MainAppScaffold()
            }
        }
    }
}
