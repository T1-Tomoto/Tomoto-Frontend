package com.example.tomoto.structure.bottombarcontents.settings.uiconponents

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ErrorDialog(
    errorMessage: String,
    onDismiss: () -> Unit
) {
    if (errorMessage.isNotBlank()) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "에러") },
            text = { Text(text = errorMessage) },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("확인")
                }
            }
        )
    }
}