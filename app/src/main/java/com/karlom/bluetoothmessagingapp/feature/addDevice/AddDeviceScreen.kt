package com.karlom.bluetoothmessagingapp.feature.addDevice

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.karlom.bluetoothmessagingapp.R
import com.karlom.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent
import com.karlom.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent.OnDeviceClicked
import com.karlom.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent.OnDiscoverableSwitchChecked
import com.karlom.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent.OnScanForDevicesClicked
import com.karlom.bluetoothmessagingapp.feature.addDevice.viewmodel.AddDeviceViewModel
import com.karlom.bluetoothmessagingapp.feature.addDevice.components.BluetoothDeviceItem
import com.karlom.bluetoothmessagingapp.feature.shared.SimpleLazyColumn

@Composable
fun AddDeviceScreen(
    viewModel: AddDeviceViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val bluetoothDevices = state.bluetoothDevices.collectAsLazyPagingItems()
    val makeDiscoverable = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode > 0) viewModel.onEvent(OnDiscoverableSwitchChecked)
        })
    val makeDeviceDiscoverablePermission = rememberMultiplePermissionsState(
        permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_SCAN,
            )
        } else {
            listOf(
                Manifest.permission.BLUETOOTH_ADMIN
            )
        }
    ) { permissionAccepted ->
        if (permissionAccepted.filter { false }.isEmpty()) {
            makeDiscoverable.launch(Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE))
        }
    }
    val findBluetoothDevicesPermission = rememberMultiplePermissionsState(
        permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(Manifest.permission.BLUETOOTH_SCAN)
        } else {
            listOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        }
    ) { result ->
        if (result.filter { entry -> !entry.value }.isEmpty()) {
            viewModel.onEvent(OnScanForDevicesClicked)
        }
    }

    Column(Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .align(Alignment.End)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.add_device_screen_visible_label),
                modifier = Modifier.padding(end = 8.dp),
            )
            Switch(
                checked = state.isDiscoverable,
                enabled = !state.isDiscoverable,
                onCheckedChange = { makeDeviceDiscoverablePermission.launchMultiplePermissionRequest() },
            )
        }
        Text(
            text = stringResource(R.string.add_device_screen_bluetooth_devices_title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(start = 8.dp)
        )
        if (!state.isBluetoothDeviceListShown) {
            Box(
                modifier = Modifier
                    .weight(weight = 1f, fill = true)
                    .fillMaxWidth(),
            ) {
                Button(
                    onClick = { findBluetoothDevicesPermission.launchMultiplePermissionRequest() },
                    modifier = Modifier.align(Alignment.Center),
                ) {
                    Text(text = stringResource(R.string.add_device_screen_start_search_button))
                }
            }
        } else {
            SimpleLazyColumn(
                items = bluetoothDevices,
                key = { address },
                uiItemBuilder = { device ->
                    BluetoothDeviceItem(
                        device = device,
                        modifier = Modifier.clickable { viewModel.onEvent(OnDeviceClicked(device.address)) }
                    )
                },
                noItemsItem = {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = stringResource(R.string.bluetooth_device_screen_no_devices_nearby),
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }
                },
            )
        }
    }
}
