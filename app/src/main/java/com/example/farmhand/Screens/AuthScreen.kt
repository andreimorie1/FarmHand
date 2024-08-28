package com.example.farmhand.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.farmhand.authentication.authModels.AuthViewModel
import com.example.farmhand.authentication.composables.FormSelection
import com.example.farmhand.authentication.composables.authButton
import com.example.farmhand.authentication.composables.authForm
import com.example.farmhand.ui.theme.Typography


@Composable
fun AuthScreen(authViewModel: AuthViewModel) {
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
        Spacer(modifier = Modifier.height(170.dp))

        FormSelection(viewModel = authViewModel)

        Spacer(modifier = Modifier.height(40.dp))

        authForm(viewModel = authViewModel)

        Spacer(modifier = Modifier.height(50.dp))
        authButton(
            value = if (authViewModel.isSignUP) "Sign Up" else "Sign In",
            onClick = { authViewModel.onAuthButtonClick() }
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun AuthScreenPreview() {
    val authModel = AuthViewModel()
    AuthScreen(authViewModel = authModel)
}