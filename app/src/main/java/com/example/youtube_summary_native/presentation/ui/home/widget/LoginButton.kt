package com.example.youtube_summary_native.core.presentation.ui.common

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.example.youtube_summary_native.core.constants.AppDimensions

@Composable
fun LoginButton(
    onLoginClick: () -> Unit,
    isOfflineMode: Boolean,
    modifier: Modifier = Modifier,
    text: String = "로그인"
) {
    ElevatedButton(
        onClick = onLoginClick,
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
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}