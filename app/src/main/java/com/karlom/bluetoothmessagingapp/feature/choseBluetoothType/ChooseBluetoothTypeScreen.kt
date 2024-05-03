package com.karlom.bluetoothmessagingapp.feature.choseBluetoothType

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ChooseBluetoothTypeScreen() {
    val bluetoothController = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result -> }
    )
    Box(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = {
                val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                bluetoothController.launch(enableIntent)
            },
            modifier = Modifier.align(Alignment.Center)
        ) { Text(text = "Start searching") }
    }
}
