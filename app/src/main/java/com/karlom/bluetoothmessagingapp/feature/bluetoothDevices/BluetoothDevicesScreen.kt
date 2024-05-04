package com.karlom.bluetoothmessagingapp.feature.bluetoothDevices

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun BluetoothDevicesScreen() {
    Box(Modifier.fillMaxSize()) {
        Text(
            text = "Bluetooth devices screen",
            modifier = Modifier.align(Alignment.Center),
        )
    }
}
