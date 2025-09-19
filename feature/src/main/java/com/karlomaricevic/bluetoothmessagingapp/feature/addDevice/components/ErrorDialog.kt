package com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.karlomaricevic.bluetoothmessagingapp.designsystem.BluetoothMessagingAppTheme

@Composable
fun ErrorDialog(
    title: String,
    message: String,
    confirmButtonText: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        text = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = onDismiss) {
                    Text(confirmButtonText)
                }
            }
        }
    )
}

@Preview
@Composable
private fun ErrorDialogPreview() {
    BluetoothMessagingAppTheme {
        ErrorDialog(
            title = "Device not visible",
            message = "Couldn’t make your device discoverable. Check Bluetooth settings and try again.",
            confirmButtonText = "OK"
        ) { }
    }
}
