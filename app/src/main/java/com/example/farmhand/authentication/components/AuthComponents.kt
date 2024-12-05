package com.example.farmhand.authentication.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.farmhand.module_user.models.UserViewModel
import com.example.farmhand.ui.theme.Typography


//Form +AuthTextField
@Composable
fun AuthForm(
    userViewModel: UserViewModel,
    lastname: String,
    onLastnameChange: (String) -> Unit,
    isSignUP: Boolean,
    username: String,
    onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityToggle: () -> Unit,
    password2: String,
    onPasswordChange2: (String) -> Unit,
    passwordVisible2: Boolean,
    onPasswordVisibilityToggle2: () -> Unit
) {
    Column {
        AuthTextField(
            value = username,
            onValueChange = onUsernameChange,
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
                    imageVector = Icons.Rounded.Person,
                    contentDescription = "Username"
                )
            }
        )

        if (isSignUP) {
            AuthTextField(
                value = lastname,
                onValueChange = onLastnameChange,
                label = { Text(text = "Last name", style = Typography.labelMedium) },
                placeholder = {
                    Text(
                        text = "Start Typing",
                        style = Typography.labelSmall,
                        fontWeight = FontWeight.ExtraLight
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Person,
                        contentDescription = "lastname"
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(5.dp))
        AuthTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text(text = "Password", style = Typography.labelMedium) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Lock,
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
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image =
                    if (passwordVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff
                val description =
                    if (passwordVisible) "Hide Password" else "Show Password"
                IconButton(onClick = onPasswordVisibilityToggle) {
                    Icon(imageVector = image, contentDescription = description)
                }
            }
        )
    }
if (isSignUP){
    AuthTextField(
        value = password2,
        onValueChange = onPasswordChange2,
        label = { Text(text = "Re-Type Password", style = Typography.labelMedium) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Lock,
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
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image =
                if (passwordVisible2) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff
            val description =
                if (passwordVisible2) "Hide Password" else "Show Password"
            IconButton(onClick = onPasswordVisibilityToggle2) {
                Icon(imageVector = image, contentDescription = description)
            }
        }
    )
}
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
            if (userViewModel.response?.message.equals("Success")){
                Text(
                    text = userViewModel.response?.message ?: "",
                    color = Color.Green,
                    style = Typography.labelMedium,
                )
            }
        }

/*
        for (error in authViewModel.errorMessages) {
            Text(
                text = error,
                color = if (error == "User Registered Successfully") androidx.compose.ui.graphics.Color.Green else androidx.compose.ui.graphics.Color.Red,
                style = Typography.labelMedium,
            )
        }
 */
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
fun AuthButton(
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
    isSignUP: Boolean,
    onSignInClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(
            onClick = onSignInClick
        ) {
            Text(
                text = "Sign In",
                style = Typography.titleLarge,
                fontWeight = if (isSignUP) FontWeight.Normal else FontWeight.ExtraBold
            )
        }
        TextButton(
            onClick = onSignUpClick
        ) {
            Text(
                text = "Sign Up",
                style = Typography.titleLarge,
                fontWeight = if (isSignUP) FontWeight.ExtraBold else FontWeight.Normal
            )
        }
    }
}
