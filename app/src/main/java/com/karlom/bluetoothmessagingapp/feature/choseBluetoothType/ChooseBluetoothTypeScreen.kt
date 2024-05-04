package com.karlom.bluetoothmessagingapp.feature.choseBluetoothType

import android.Manifest
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.karlom.bluetoothmessagingapp.feature.choseBluetoothType.models.ChooseBluetoothTypeScreenEvent.OnSearchBluetoothDevicesClicked
import com.karlom.bluetoothmessagingapp.feature.choseBluetoothType.viewmodel.ChooseBluetoothTypeViewModel

@Composable
fun ChooseBluetoothTypeScreen(
    viewModel: ChooseBluetoothTypeViewModel = hiltViewModel(),
) {
    val makeDiscoverable =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                // TODO
            }
        }
    val makeDeviceDiscoverablePermission = rememberPermissionState(
        permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Manifest.permission.BLUETOOTH_ADVERTISE
        } else {
            Manifest.permission.BLUETOOTH_ADMIN
        }
    ) { permissionAccepted ->
        if (permissionAccepted) {
            makeDiscoverable.launch(Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
            })
        }
    }
    val bluetoothController = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == RESULT_OK) {
                makeDeviceDiscoverablePermission.launchPermissionRequest()
            }
        }
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Button(
            onClick = { viewModel.onEvent(OnSearchBluetoothDevicesClicked) },
            modifier = Modifier.padding(bottom = 16.dp),
        ) { Text(text = "Start searching") }
        Button(onClick = {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            bluetoothController.launch(enableIntent)
        }) {
            Text(text = "Make discoverable")
        }
    }
}
