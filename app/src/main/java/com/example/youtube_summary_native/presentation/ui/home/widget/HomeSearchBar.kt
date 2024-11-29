package com.example.youtube_summary_native.presentation.ui.home.widget

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.youtube_summary_native.core.constants.AppDimensions
import com.example.youtube_summary_native.core.domain.model.state.HomeScreenState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear

@Composable
fun HomeSearchBar(
    homeScreenState: HomeScreenState,
    onTextChanged: (String) -> Unit,
    onTextClear: () -> Unit,
    onPaste: () -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    isOfflineMode: Boolean = false
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val searchText = remember { mutableStateOf("") }

    // Back handler for search focus
    BackHandler(enabled = homeScreenState.searchBarState.isFocused) {
        focusManager.clearFocus()
        onFocusChanged(false)
    }

    val transition = updateTransition(
        targetState = homeScreenState.searchBarState.isEmpty && !homeScreenState.searchBarState.isFocused,
        label = "searchBarTransition"
    )

    val textVisibilityAlpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 300, easing = LinearEasing) },
        label = "textVisibility"
    ) { visible -> if (visible) 1f else 0f }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AppDimensions.LargePadding)
    ) {
        // Search Bar Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(AppDimensions.SearchBarRadius)
                )
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(AppDimensions.SearchBarRadius)
                )
                .padding(AppDimensions.DefaultPadding)
        ) {
            // Text Field
            BasicTextField(
                value = searchText.value,
                onValueChange = { newText ->
                    searchText.value = newText
                    onTextChanged(newText)
                },
                textStyle = TextStyle(
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        if (!focusState.isFocused && homeScreenState.searchBarState.isFocused) {
                            // 포커스가 해제될 때만 콜백 호출
                            onFocusChanged(false)
                        } else if (focusState.isFocused && !homeScreenState.searchBarState.isFocused) {
                            // 포커스를 얻을 때만 콜백 호출
                            onFocusChanged(true)
                        }
                    },
                enabled = !isOfflineMode,
                decorationBox = { innerTextField ->
                    Box(
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (searchText.value.isEmpty() && !homeScreenState.searchBarState.isFocused) {
                            Text(
                                text = homeScreenState.clipboardData.takeIf { it.isNotEmpty() }
                                    ?: "YouTube URL을 입력하세요",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                        innerTextField()

                        // Clear or Paste Button
                        if (searchText.value.isNotEmpty() || homeScreenState.clipboardData.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    if (searchText.value.isNotEmpty()) {
                                        onTextClear()
                                        searchText.value = ""
                                    } else {
                                        onPaste()
                                    }
                                },
                                modifier = Modifier.align(Alignment.CenterEnd)
                            ) {
                                Icon(
                                    imageVector = if (searchText.value.isNotEmpty())
                                        Icons.Default.Clear else Icons.Default.Call,
                                    contentDescription = if (searchText.value.isNotEmpty())
                                        "Clear text" else "Paste",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(AppDimensions.IconSize)
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}