package com.example.youtube_summary_native.presentation.ui.summary.widget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.youtube_summary_native.core.constants.AppDimensions
import com.example.youtube_summary_native.presentation.theme.AppColors
import kotlinx.coroutines.launch

@Composable
fun ScriptWidget(
    scriptData: String?,
    isLoading: Boolean,
    hasError: Boolean,
    onTimeClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppDimensions.CardRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Copy Button
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface
            ) {
                TextButton(
                    onClick = {
                        scriptData?.let {
                            scope.launch {
                                clipboardManager.setText(AnnotatedString(it))
                            }
                        }
                    },
                    enabled = !isLoading && !hasError && !scriptData.isNullOrEmpty(),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Copy to clipboard",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("클립보드에 복사")
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    hasError -> {
                        Text(
                            text = "스크립트를 불러오는데 실패했습니다.",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    scriptData == null || scriptData.isEmpty() -> {
                        Text(
                            text = "요약 스크립트를 준비중입니다.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    else -> {
                        val processedText = remember(scriptData) {
                            processMarkdownText(scriptData)
                        }

                        val annotatedText = remember(processedText) {
                            buildAnnotatedString {
                                val timePattern = Regex("""\[(\d{2}:\d{2}:\d{2})\]""")
                                var lastIndex = 0

                                timePattern.findAll(processedText).forEach { matchResult ->
                                    // Add text before timestamp
                                    append(processedText.substring(lastIndex, matchResult.range.first))

                                    // Add timestamp with style
                                    val timestamp = matchResult.groupValues[1]
                                    withStyle(
                                        style = SpanStyle(
                                            color = AppColors.Primary,
                                            textDecoration = TextDecoration.Underline
                                        )
                                    ) {
                                        append("[${timestamp}]")
                                    }

                                    lastIndex = matchResult.range.last + 1
                                }

                                // Add remaining text
                                if (lastIndex < processedText.length) {
                                    append(processedText.substring(lastIndex))
                                }
                            }
                        }

                        SelectionContainer {
                            ClickableText(
                                text = annotatedText,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                onClick = { offset ->
                                    val timePattern = Regex("""\[(\d{2}:\d{2}:\d{2})\]""")
                                    val text = processedText.substring(
                                        maxOf(0, offset - 10),
                                        minOf(processedText.length, offset + 10)
                                    )
                                    timePattern.find(text)?.groupValues?.get(1)?.let { timeStr ->
                                        onTimeClick(parseTimeToSeconds(timeStr))
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun parseTimeToSeconds(timeStr: String): Long {
    val parts = timeStr.split(":")
    return parts[0].toLong() * 3600 + // hours
            parts[1].toLong() * 60 +   // minutes
            parts[2].toLong()          // seconds
}

private fun processMarkdownText(text: String): String {
    // 타임라인 형식 변환 (HH:MM:SS - HH:MM:SS 형식)
    var processedText = text.replace(
        Regex("""\*\*(\d{2}:\d{2}:\d{2})\s*-\s*\d{2}:\d{2}:\d{2}\*\*:\s*(.*)"""),
        "[$1] $2"
    )

    // (time//시간) 형식 제거
    processedText = processedText.replace(
        Regex("""\(time://[^\)]+\)"""),
        ""
    )

    // [HH:MM:SS] 형식의 타임라인 처리
    processedText = processedText.replace(
        Regex("""\[(\d{2}:\d{2}:\d{2})\](.*)"""),
        "\n[$1]$2\n"
    )

    // 글머리 기호 앞에 줄바꿈 추가
    processedText = processedText.replace("•", "\n\n•")

    // 연속된 줄바꿈 정리
    processedText = processedText.replace(Regex("\n{3,}"), "\n\n")

    return processedText.trim()
}