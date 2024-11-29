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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.youtube_summary_native.core.constants.AppDimensions
import com.example.youtube_summary_native.core.presentation.auth.AuthViewModel
import com.example.youtube_summary_native.presentation.ui.auth.state.AuthEvent
import com.example.youtube_summary_native.presentation.ui.auth.state.AuthState
import com.example.youtube_summary_native.presentation.ui.auth.widget.AuthTextField

@Composable
fun RegisterScreen(
    onLoginRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    // 회원가입 성공 시 처리할 효과
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.RegistrationSuccess -> {
                onLoginRequest() // 회원가입 성공 후 로그인 화면으로 이동
            }
            else -> {} // 필요한 경우 다른 상태를 처리
        }
    }

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

        // 오류 메시지 표시
        uiState.errorMessage?.let { error ->
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

            // 사용자 이름 입력 필드
            AuthTextField(
                value = uiState.username,
                onValueChange = { viewModel.onEvent(AuthEvent.OnUsernameChange(it)) },
                label = "사용자 이름",
                leadingIcon = Icons.Rounded.Person,
                enabled = !uiState.isLoading
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

            // 비밀번호 입력 필드
            AuthTextField(
                value = uiState.password,
                onValueChange = { viewModel.onEvent(AuthEvent.OnPasswordChange(it)) },
                label = "비밀번호",
                leadingIcon = Icons.Rounded.Lock,
                isPassword = true,
                isPasswordVisible = isPasswordVisible,
                onTogglePasswordVisibility = { isPasswordVisible = !isPasswordVisible },
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(AppDimensions.DefaultPadding))

            // 비밀번호 확인 입력 필드
            AuthTextField(
                value = uiState.confirmPassword,
                onValueChange = { viewModel.onEvent(AuthEvent.OnConfirmPasswordChange(it)) },
                label = "비밀번호 확인",
                leadingIcon = Icons.Rounded.Lock,
                isPassword = true,
                isPasswordVisible = isConfirmPasswordVisible,
                onTogglePasswordVisibility = { isConfirmPasswordVisible = !isConfirmPasswordVisible },
                enabled = !uiState.isLoading
            )
        }

        Spacer(modifier = Modifier.height(AppDimensions.LargePadding))

        // 회원가입 버튼
        Button(
            onClick = { viewModel.onEvent(AuthEvent.OnRegister) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !uiState.isLoading &&
                    uiState.username.isNotEmpty() &&
                    uiState.password.isNotEmpty() &&
                    uiState.confirmPassword.isNotEmpty(),
            shape = MaterialTheme.shapes.medium
        ) {
            if (uiState.isLoading) {
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

        // 로그인 요청 버튼
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
