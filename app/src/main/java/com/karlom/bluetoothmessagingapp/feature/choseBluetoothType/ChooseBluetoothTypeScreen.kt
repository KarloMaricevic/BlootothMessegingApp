package com.karlom.bluetoothmessagingapp.feature.choseBluetoothType

import android.Manifest
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@Composable
fun ChooseBluetoothTypeScreen() {
    val findBluetoothDevicesPermission = rememberMultiplePermissionsState(
        permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(Manifest.permission.BLUETOOTH_SCAN)
        } else {
            listOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    ) { result ->
        if (result.filter { entry -> !entry.value }.isEmpty()) {
            // TODO
        }
    }
    val bluetoothController = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == RESULT_OK) {
                findBluetoothDevicesPermission.launchMultiplePermissionRequest()
            }
        }
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
