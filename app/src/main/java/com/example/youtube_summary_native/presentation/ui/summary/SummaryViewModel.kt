package com.example.youtube_summary_native.presentation.ui.summary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val videoId: String = checkNotNull(savedStateHandle["videoId"])
}