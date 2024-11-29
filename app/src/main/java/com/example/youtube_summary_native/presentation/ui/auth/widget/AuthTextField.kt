package com.example.youtube_summary_native.presentation.ui.auth.widget

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    hint: String? = null,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onTogglePasswordVisibility: (() -> Unit)? = null,
    onSubmit: (() -> Unit)? = null,
    errorMessage: String? = null,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { onTogglePasswordVisibility?.invoke() }) {
                    Icon(
                        imageVector = if (isPasswordVisible) {
                            Icons.Rounded.Visibility
                        } else {
                            Icons.Rounded.VisibilityOff
                        },
                        contentDescription = if (isPasswordVisible) {
                            "비밀번호 숨기기"
                        } else {
                            "비밀번호 표시"
                        },
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else null,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        visualTransformation = if (isPassword && !isPasswordVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        keyboardOptions = KeyboardOptions(
            imeAction = if (onSubmit != null) ImeAction.Done else ImeAction.Next,
            keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text
        ),
        keyboardActions = KeyboardActions(
            onDone = { onSubmit?.invoke() }
        ),
        singleLine = true,
        isError = errorMessage != null,
        supportingText = errorMessage?.let { { Text(text = it) } },
        shape = MaterialTheme.shapes.medium,
        placeholder = hint?.let { { Text(text = it) } }
    )
}