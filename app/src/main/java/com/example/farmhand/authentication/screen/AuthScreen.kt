package com.example.farmhand.authentication.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.farmhand.authentication.components.AuthButton
import com.example.farmhand.authentication.components.AuthForm
import com.example.farmhand.authentication.components.FormSelection
import com.example.farmhand.authentication.models.AuthViewModel
import com.example.farmhand.module_user.api.LoginRequest
import com.example.farmhand.module_user.api.RegisterRequest
import com.example.farmhand.module_user.models.UserViewModel
import com.example.farmhand.navigation.AuthManager
import com.example.farmhand.ui.theme.Typography


@Composable
fun AuthScreen(navController: NavHostController, userViewModel: UserViewModel) {
    val context = LocalContext.current

    // Obtain the ViewModel instance
    val authViewModel: AuthViewModel = hiltViewModel()
    val scrollState = rememberScrollState()

    DisposableEffect(Unit) {
        onDispose {
            userViewModel.resetErrorMessage()
        }
    }
    // Composable code
    Column(
        modifier = Modifier
            .padding(start = 40.dp, end = 40.dp)
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(scrollState),
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
                    userViewModel.resetData()
                }
                authViewModel.isSignUP = false
            },
            onSignUpClick = {
                if (!authViewModel.isSignUP) {
                    authViewModel.fieldReset()
                    userViewModel.resetData()
                }
                authViewModel.isSignUP = true
            }
        )

        Spacer(modifier = Modifier.height(40.dp))

        AuthForm(
            userViewModel = userViewModel,
            isSignUP = authViewModel.isSignUP,
            lastname = authViewModel.lastname,
            onLastnameChange = authViewModel::onLastnameChange,
            username = authViewModel.username,
            onUsernameChange = authViewModel::onUsernameChange,
            password = authViewModel.password,
            onPasswordChange = authViewModel::onPasswordChange,
            passwordVisible = authViewModel.passwordVisible,
            onPasswordVisibilityToggle = authViewModel::onPasswordVisibilityToggle,
            password2 = authViewModel.rePassword,
            onPasswordChange2 = authViewModel::onRePasswordChange,
            passwordVisible2 = authViewModel.rePasswordVisible2,
            onPasswordVisibilityToggle2 = authViewModel::onPasswordVisibilityToggle2,

            )
        Spacer(modifier = Modifier.height(10.dp))

        if (userViewModel.response?.login == true) {
            AuthManager.saveAuthState(context = context, isLoggedIn = userViewModel.response?.login == true, userViewModel.response?.farmerId ?: -1)

            navController.navigate("main") {
                popUpTo("main") { inclusive = true } // Clear backstack
            }
            Log.d("AuthScreen", "Authentication Success")
        }

        Spacer(modifier = Modifier.height(50.dp))
        AuthButton(
            value = if (authViewModel.isSignUP) "Sign Up" else "Sign In",
            onClick = {
                if (authViewModel.isSignUP) {
                    if (authViewModel.password == authViewModel.rePassword) {
                        userViewModel.registerFarmer(
                            RegisterRequest(
                                firstname = authViewModel.username,
                                lastname = authViewModel.lastname,
                                password = authViewModel.password
                            )
                        )
                        authViewModel.clearError()
                    } else {
                        authViewModel.passwordError()
                    }
                } else {
                    navController.navigate("main") {
                        popUpTo("main") { inclusive = true } // Clear backstack
                    }
                    userViewModel.loginFarmer(
                        LoginRequest(
                            firstname = authViewModel.username,
                            password = authViewModel.password
                        )
                    )
                }
            }//authViewModel::onAuthButtonClick
        )
    }
}
