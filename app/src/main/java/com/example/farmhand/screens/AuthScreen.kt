package com.example.farmhand.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.farmhand.components.AuthButton
import com.example.farmhand.components.AuthForm
import com.example.farmhand.components.FormSelection
import com.example.farmhand.components.ReTypePassword
import com.example.farmhand.models.AuthViewModel
import com.example.farmhand.ui.theme.Typography


@Composable
fun AuthScreen(navController: NavHostController) {
    // Obtain the ViewModel instance
    val authViewModel: AuthViewModel = hiltViewModel()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

    // Composable code
    Column(
        modifier = Modifier
            .padding(start = 40.dp, end = 40.dp)
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "FarmHand",
            style = Typography.displayMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(140.dp))

        //Sign in or Sign up Form Selection
        FormSelection(
            isSignUP = authViewModel.isSignUP,
            onSignInClick = {
                if (authViewModel.isSignUP) {
                    authViewModel.fieldReset()
                }
                authViewModel.isSignUP = false
            },
            onSignUpClick = {
                if (!authViewModel.isSignUP) {
                    authViewModel.fieldReset()
                }
                authViewModel.isSignUP = true
            }
        )

        Spacer(modifier = Modifier.height(40.dp))

        AuthForm(
            username = authViewModel.username,
            onUsernameChange = authViewModel::onUsernameChange,
            password = authViewModel.password,
            onPasswordChange = authViewModel::onPasswordChange,
            passwordVisible = authViewModel.passwordVisible,
            onPasswordVisibilityToggle = authViewModel::onPasswordVisibilityToggle
        )
        Spacer(modifier = Modifier.height(10.dp))
        if (authViewModel.isSignUP) {
            ReTypePassword(
                password = authViewModel.rePassword,
                onPasswordChange = authViewModel::onRePasswordChange,
                passwordVisible = authViewModel.rePasswordVisible2,
                onPasswordVisibilityToggle = authViewModel::onPasswordVisibilityToggle2
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            for (error in authViewModel.errorMessages) {
                Text(
                    text = error,
                    color = if (error == "User Registered Successfully") androidx.compose.ui.graphics.Color.Green else androidx.compose.ui.graphics.Color.Red,
                    style = Typography.labelMedium,
                )
            }
        }



        if (isAuthenticated) {
            navController.navigate("main") {
                popUpTo("main") { inclusive = true } // Clear backstack
            }
            Log.d("AuthScreen", "Authentication Success ${isAuthenticated}")
        }
        Spacer(modifier = Modifier.height(50.dp))
        AuthButton(
            value = if (authViewModel.isSignUP) "Sign Up" else "Sign In",
            onClick = authViewModel::onAuthButtonClick
        )
    }
}
