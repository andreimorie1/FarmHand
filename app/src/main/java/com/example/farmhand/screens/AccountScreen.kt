package com.example.farmhand.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.farmhand.components.uAccountButtons
import com.example.farmhand.components.uAccountField
import com.example.farmhand.models.uAccountModel
import com.example.farmhand.ui.theme.Typography

@Composable
fun AccountScreen(
    uAccountViewModel: uAccountModel = hiltViewModel()
) {
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
        uAccountButtons()
    }
}

@Preview(showSystemUi = true)
@Composable
fun AccountScreenPreveiw() {
    AccountScreen()
}