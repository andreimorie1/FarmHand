package com.example.farmhand.screens

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.farmhand.components.UAccountButtons
import com.example.farmhand.components.uAccountField
import com.example.farmhand.models.AuthViewModel
import com.example.farmhand.models.uAccountModel
import com.example.farmhand.models_shared.SharedViewModel
import com.example.farmhand.ui.theme.Typography
import dagger.hilt.android.qualifiers.ApplicationContext

@Composable
fun AccountScreen(
    navController: NavHostController,
    ) {
    val uAccountViewModel: uAccountModel = hiltViewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 25.dp, end = 25.dp, top = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "My Account",
            style = Typography.displayMedium,
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "ID",
            style = Typography.labelMedium,
        )
        // User ID
        Text(
            text = uAccountViewModel.userID.toString(),
            style = Typography.headlineMedium,
        )

        Spacer(modifier = Modifier.height(25.dp))
        // username TextField
        uAccountField(
            value = uAccountViewModel.username,
            onValueChange = uAccountViewModel::onUsernameChange,
            label = { Text(text = "Username", style = Typography.labelMedium) },
            placeholder = {
                Text(
                    text = "Start Typing",
                    style = Typography.labelSmall,
                    fontWeight = FontWeight.ExtraLight
                )
            },
        )

        Spacer(modifier = Modifier.height(25.dp))
        // Password TextField
        uAccountField(
            value = uAccountViewModel.password,
            onValueChange = uAccountViewModel::onPasswordChange,
            label = { Text(text = "Password", style = Typography.labelMedium) },
            placeholder = {
                Text(
                    text = "Start Typing",
                    style = Typography.labelSmall,
                    fontWeight = FontWeight.ExtraLight
                )
            },
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Buttons
        UAccountButtons(
            onLogOut = {
                uAccountViewModel.onLogOut()
                navController.navigate("auth") {
                    popUpTo(0) // Clears the back stack to prevent returning to the previous screen
                }
            },
            onUpdate = { /* Handle update button click */ }
        )


/*
        if (!isAuthenticated) {
            // Navigate to main screen when authentication is successful
            LaunchedEffect(isAuthenticated) {
                navController.navigate("main") {
                    popUpTo("auth") { inclusive = true } // Clear backstack
                }
            }
        }
 */
    }
}
