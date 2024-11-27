package com.example.youtube_summary_native.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.youtube_summary_native.core.presentation.ui.home.HomeScreen
import com.example.youtube_summary_native.presentation.theme.YouTubeSummaryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            YouTubeSummaryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen(
                        onNavigateToSummary = { /* Summary Screen Navigation 구현 시 추가 */ },
                        onNavigateToAuth = { /* Auth Screen Navigation 구현 시 추가 */ }
                    )
                }
            }
        }
    }
}