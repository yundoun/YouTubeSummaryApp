package com.example.youtube_summary_native.presentation.ui.home

import androidx.compose.runtime.Composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun HomeScreen() {

    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Home Screen")
    }
}