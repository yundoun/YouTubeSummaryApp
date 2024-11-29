package com.example.youtube_summary_native.core.presentation.ui.auth.screens

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
fun RegisterScreen(
    onLoginRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Rounded.PersonAdd,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(AppDimensions.DefaultPadding))

        Text(
            text = "새로운 계정 만들기",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(AppDimensions.ExtraSmallPadding))

        Text(
            text = "아래 정보를 입력하여 계정을 생성하세요",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(AppDimensions.DefaultPadding))
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

        Spacer(modifier = Modifier.height(AppDimensions.MediumPadding))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "계정 정보",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(AppDimensions.ExtraSmallPadding))

            Text(
                text = "사용자 이름은 4글자 이상 영문/숫자 조합이어야 합니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(AppDimensions.DefaultPadding))

            AuthTextField(
                value = username,
                onValueChange = { username = it },
                label = "사용자 이름",
                leadingIcon = Icons.Rounded.Person,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(AppDimensions.MediumPadding))

            Text(
                text = "비밀번호",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(AppDimensions.ExtraSmallPadding))

            Text(
                text = "비밀번호는 8자 이상의 영문, 숫자, 특수문자 조합이어야 합니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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

            Spacer(modifier = Modifier.height(AppDimensions.DefaultPadding))

            AuthTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "비밀번호 확인",
                leadingIcon = Icons.Rounded.Lock,
                isPassword = true,
                isPasswordVisible = isConfirmPasswordVisible,
                onTogglePasswordVisibility = { isConfirmPasswordVisible = !isConfirmPasswordVisible },
                enabled = !isLoading
            )
        }

        Spacer(modifier = Modifier.height(AppDimensions.LargePadding))

        Button(
            onClick = { /* TODO: Implement register logic */ },
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
                    text = "회원가입",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(AppDimensions.DefaultPadding))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "이미 계정이 있으신가요?",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = onLoginRequest) {
                Text(
                    text = "로그인하기",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = MaterialTheme.typography.titleMedium.fontWeight
                    )
                )
            }
        }
    }
}