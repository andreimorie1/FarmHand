package com.example.farmhand.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.farmhand.ui.theme.Typography


//TextField
@Composable
fun uAccountField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
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

@Composable
fun UAccountButtons(
    onLogOut: () -> Unit,
    onUpdate: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Log Out Button
        Button(
            onClick = onLogOut,
            modifier = Modifier
                .weight(2f)
                .wrapContentWidth(),

            ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Logout,
                contentDescription = "Edit"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Log Out",
                style = Typography.bodyLarge
            )
        }

        // Update Account Button
        FilledTonalButton(
            onClick = onUpdate,
            modifier = Modifier
                .weight(2f)
                .wrapContentWidth(),

            ) {
            Text(
                text = "Update Account",
                style = Typography.bodyLarge
            )
        }
    }
}

