package com.example.youtube_summary_native.presentation.ui.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.youtube_summary_native.core.constants.AppDimensions
import com.example.youtube_summary_native.core.presentation.ui.auth.screens.RegisterScreen
import com.example.youtube_summary_native.presentation.ui.auth.screens.LoginScreen

@Composable
fun AuthScreen(
    onDismissRequest: () -> Unit,
) {
    var showLogin by remember { mutableStateOf(true) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppDimensions.LargePadding),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .widthIn(max = 500.dp)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 8.dp
                )
            ) {
                AnimatedContent(
                    targetState = showLogin,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(400)) togetherWith
                                fadeOut(animationSpec = tween(400))
                    },
                    modifier = Modifier.padding(AppDimensions.LargePadding)
                ) { isLogin ->
                    if (isLogin) {
                        LoginScreen(
                            onRegisterRequest = { showLogin = false },
                            onDismiss = onDismissRequest
                        )
                    } else {
                        RegisterScreen(
                            onLoginRequest = { showLogin = true },
                            onDismiss = onDismissRequest
                        )
                    }
                }
            }
        }
    }
}