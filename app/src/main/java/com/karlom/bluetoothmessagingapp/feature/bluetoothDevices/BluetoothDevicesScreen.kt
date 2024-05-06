package com.karlom.bluetoothmessagingapp.feature.bluetoothDevices

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.karlom.bluetoothmessagingapp.R
import com.karlom.bluetoothmessagingapp.feature.bluetoothDevices.components.BluetoothDeviceItem
import com.karlom.bluetoothmessagingapp.feature.bluetoothDevices.models.BluetoothDevicesScreenEvent
import com.karlom.bluetoothmessagingapp.feature.bluetoothDevices.models.BluetoothDevicesScreenEvent.*
import com.karlom.bluetoothmessagingapp.feature.bluetoothDevices.viewmodel.BluetoothDevicesViewModel
import com.karlom.bluetoothmessagingapp.feature.shared.SimpleLazyColumn

@Composable
fun BluetoothDevicesScreen(
    viewModel: BluetoothDevicesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val devices = state.devices.collectAsLazyPagingItems()
    val findBluetoothDevicesPermission = rememberMultiplePermissionsState(
        permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(Manifest.permission.BLUETOOTH_SCAN)
        } else {
            listOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        }
    ) { result ->
        if (result.filter { entry -> !entry.value }.isEmpty()) {
            viewModel.onEvent(OnScanForDevicesClicked)
        }
    }
    val bluetoothController =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult(),
            onResult = { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    findBluetoothDevicesPermission.launchMultiplePermissionRequest()
                }
            })

    if (state.showSearchButton) {
        Box(modifier = Modifier.fillMaxSize()) {
            Button(
                onClick = {
                    val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    bluetoothController.launch(enableIntent)
                },
                modifier = Modifier.align(Alignment.Center),
            ) { Text(text = stringResource(R.string.bluetooth_devices_screen_start_searching)) }
        }
    } else {
        SimpleLazyColumn(
            items = devices,
            key = { address },
            uiItemBuilder = { device ->
                BluetoothDeviceItem(
                    device = device,
                    modifier = Modifier.clickable {
                        viewModel.onEvent(
                            OnBluetoothDeviceClicked(device.address)
                        )
                    },
                )
            },
            noItemsItem = {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = stringResource(R.string.bluetooth_device_screen_no_devices_nearby),
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }
        )
    }
}
