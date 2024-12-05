package com.example.farmhand.module_user.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.farmhand.module_user.api.IdRequest
import com.example.farmhand.module_user.components.UAccountButtons
import com.example.farmhand.module_user.components.uAccountField
import com.example.farmhand.module_user.models.UserViewModel
import com.example.farmhand.module_user.models.uAccountModel
import com.example.farmhand.navigation.AuthManager
import com.example.farmhand.ui.theme.Typography

@Composable
fun AccountScreen(
    navController: NavHostController,
    userViewModel: UserViewModel
) {
    val uAccountViewModel: uAccountModel = hiltViewModel()

    val response by remember { mutableStateOf(userViewModel.response) }
    val context = LocalContext.current

    DisposableEffect(Unit) {
        onDispose {
            userViewModel.resetErrorMessage()
        }
    }

    LaunchedEffect(Unit) {
        val userId = AuthManager.getUserIdLoggedIn(context) // use AuthManager to get user ID
        Log.d("uAccountModel", "User fetched: $userId")

        val idRequest = IdRequest(userId)
        userViewModel.getFarmerById(idRequest)
        Log.d(
            "UserViewModel",
            "User fetched - username:${response?.firstname}, lastname:${response?.lastname} ID: ${response?.farmerId}"
        )

    }
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
        // INSERT User ID
        Text(
            text = userViewModel.response?.farmerId.toString(),//uAccountViewModel.userId.toString(),
            style = Typography.headlineMedium,
        )

        Spacer(modifier = Modifier.height(25.dp))
        // username TextField
        uAccountField(
            value = "",
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

        Spacer(modifier = Modifier.height(25.dp))
        // Re-type Password TextField
        uAccountField(
            value = uAccountViewModel.rePassword,
            onValueChange = uAccountViewModel::onRePasswordChange,
            label = { Text(text = "Re-type Password", style = Typography.labelMedium) },
            placeholder = {
                Text(
                    text = "Start Typing",
                    style = Typography.labelSmall,
                    fontWeight = FontWeight.ExtraLight
                )
            },
        )
        Spacer(modifier = Modifier.height(10.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            val response = userViewModel.errorMessage ?: ""
            if (userViewModel.isFetchingData) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onBackground,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(40.dp)
                    )
                }
            } else {
                Text(
                    text = response,
                    color = Color.Red,
                    style = Typography.labelMedium,
                )
                if (userViewModel.response?.message.equals("Success")) {
                    Text(
                        text = userViewModel.response?.message ?: "",
                        color = Color.Green,
                        style = Typography.labelMedium,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        // Buttons
        UAccountButtons(
            // Logout Button
            onLogOut = {
                userViewModel.onLogOutNew()
                navController.navigate("auth") {
                    popUpTo(0) // Clears the back stack to prevent returning to the previous screen
                }
            },
            // Update Account Button
            onUpdate = {
                userViewModel.updateFarmer(
                    com.example.farmhand.module_user.api.UpdateRequest(
                        id = userViewModel.response?.farmerId ?: -1,
                        firstname = uAccountViewModel.username,
                        lastname = "",
                        password = uAccountViewModel.password
                    )
                )
                //uAccountViewModel.onAccountUpdate()
            }
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
