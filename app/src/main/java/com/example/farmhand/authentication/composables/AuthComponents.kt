package com.example.farmhand.authentication.composables


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.farmhand.authentication.authModels.AuthViewModel
import com.example.farmhand.ui.theme.Typography


//Form +AuthTextField
@Composable
fun authForm(viewModel: AuthViewModel) {
    Column {
        AuthTextField(
            value = viewModel.username,
            onValueChange = viewModel::onUsernameChange,
            label = { Text(text = "Username", style = Typography.labelMedium) },
            placeholder = {
                Text(
                    text = "Start Typing",
                    style = Typography.labelSmall,
                    fontWeight = FontWeight.ExtraLight
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Password"
                )
            },

            )
        Spacer(modifier = Modifier.height(10.dp))
        AuthTextField(
            value = viewModel.password,
            onValueChange = viewModel::onPasswordChange,
            label = { Text(text = "Password", style = Typography.labelMedium) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Password"
                )
            },
            placeholder = {
                Text(
                    text = "Start Typing",
                    style = Typography.labelSmall,
                    fontWeight = FontWeight.ExtraLight
                )
            },
            visualTransformation = if (viewModel.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image =
                    if (viewModel.passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff //Change icon if password is visible
                val description =
                    if (viewModel.passwordVisible) "Hide Password" else "Show Password"
                IconButton(onClick = viewModel::onPasswordVisibilityToggle) {
                    Icon(imageVector = image, contentDescription = description)
                }
            }
        )
    }
}

//TextField
@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        trailingIcon = trailingIcon,
        leadingIcon = leadingIcon,
        placeholder = placeholder,
        visualTransformation = visualTransformation,
        textStyle = Typography.titleMedium,
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                // Draw a bottom border only
                val strokeWidth = 1.dp.toPx()
                val y = size.height - strokeWidth / 2
                drawLine(
                    color = Color.DarkGray,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            },
        shape = RoundedCornerShape(10.dp),
        maxLines = 1, // single line
        colors = TextFieldDefaults.colors( //no color
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent
        )
    )
}


//Button
@Composable
fun authButton(
    value: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 5.dp,
            pressedElevation = 7.dp
        )
    ) {
        Text(
            text = value,
            style = Typography.titleMedium,
            modifier = Modifier
                .padding(top = 2.dp, bottom = 2.dp)

        )
    }
}


@Composable
fun FormSelection(
    viewModel: AuthViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        //Log In
        TextButton(
            onClick = { viewModel.isSignUP = false },

            ) {
            Text(
                text = "Sign In",
                style = Typography.titleLarge,
                fontWeight = if (viewModel.isSignUP) FontWeight.Normal else FontWeight.ExtraBold
            )
        }

        //Sign Up
        TextButton(
            onClick = { viewModel.isSignUP = true },
        ) {
            Text(
                text = "Sign Up",
                style = Typography.titleLarge,
                fontWeight = if (viewModel.isSignUP) FontWeight.ExtraBold else FontWeight.Normal
            )
        }
    }
}