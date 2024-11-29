package com.example.youtube_summary_native.presentation.ui.auth.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.youtube_summary_native.core.constants.AppDimensions
import com.example.youtube_summary_native.presentation.ui.auth.widget.AuthTextField

@Composable
fun LoginScreen(
    onRegisterRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Icon(
                    imageVector = Icons.Rounded.Login,
                    contentDescription = null,
                    modifier = Modifier.padding(AppDimensions.DefaultPadding),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(AppDimensions.MediumPadding))

            Text(
                text = "로그인",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(AppDimensions.ExtraSmallPadding))

            Text(
                text = "서비스 이용을 위해 로그인하세요",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(AppDimensions.DefaultPadding))

        // Error message
        errorMessage?.let { error ->
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(AppDimensions.DefaultPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(AppDimensions.SmallPadding))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(AppDimensions.DefaultPadding))

        // Login Form
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "계정 정보",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(AppDimensions.DefaultPadding))

            AuthTextField(
                value = username,
                onValueChange = { username = it },
                label = "사용자 이름",
                leadingIcon = Icons.Rounded.Person,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(AppDimensions.DefaultPadding))

            Text(
                text = "비밀번호",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(AppDimensions.DefaultPadding))

            AuthTextField(
                value = password,
                onValueChange = { password = it },
                label = "비밀번호",
                leadingIcon = Icons.Rounded.Lock,
                isPassword = true,
                isPasswordVisible = isPasswordVisible,
                onTogglePasswordVisibility = { isPasswordVisible = !isPasswordVisible },
                enabled = !isLoading
            )
        }

        Spacer(modifier = Modifier.height(AppDimensions.LargePadding))

        // Login Button
        Button(
            onClick = { /* TODO: Implement login logic */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !isLoading,
            shape = MaterialTheme.shapes.medium
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = "로그인",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(AppDimensions.DefaultPadding))

        // Register Link
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "계정이 없으신가요?",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = onRegisterRequest) {
                Text(
                    text = "회원가입",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = MaterialTheme.typography.titleMedium.fontWeight
                    )
                )
            }
        }
    }
}