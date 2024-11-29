package com.example.youtube_summary_native.core.presentation.ui.common

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.youtube_summary_native.core.presentation.auth.AuthViewModel
import com.example.youtube_summary_native.presentation.ui.auth.state.AuthEvent
import com.example.youtube_summary_native.presentation.ui.auth.state.AuthState

@Composable
fun LoginButton(
    onLoginClick: () -> Unit,
    isOfflineMode: Boolean,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()

    ElevatedButton(
        onClick = {
            if (authState is AuthState.Authenticated) {
                viewModel.onEvent(AuthEvent.OnLogout)
            } else {
                onLoginClick()
            }
        },
        enabled = !isOfflineMode,
        modifier = modifier.alpha(if (isOfflineMode) 0.5f else 1f),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 2.dp
        )
    ) {
        Text(
            text = when (authState) {
                is AuthState.Authenticated -> "로그아웃"
                else -> "로그인"
            },
            style = MaterialTheme.typography.labelLarge
        )
    }
}