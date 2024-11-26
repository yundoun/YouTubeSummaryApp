package com.example.youtube_summary_native.core.presentation.ui.common.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.youtube_summary_native.core.constants.AppConstants

@Composable
fun DeleteDialog(
    isDeleteAll: Boolean = false,
    videoId: String? = null,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = AppConstants.DELETE_LABEL)
        },
        text = {
            Text(
                text = if (isDeleteAll) {
                    "모든 요약을 삭제하시겠습니까?"
                } else {
                    "이 요약을 삭제하시겠습니까?"
                }
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text("확인")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("취소")
            }
        }
    )
}