package com.karlom.bluetoothmessagingapp.feature.bluetoothDevices

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.karlom.bluetoothmessagingapp.R
import com.karlom.bluetoothmessagingapp.feature.bluetoothDevices.components.BluetoothDeviceItem
import com.karlom.bluetoothmessagingapp.feature.bluetoothDevices.models.BluetoothDevicesScreenEvent.OnScanForDevicesClicked
import com.karlom.bluetoothmessagingapp.feature.bluetoothDevices.models.BluetoothDevicesScreenEvent.*
import com.karlom.bluetoothmessagingapp.feature.bluetoothDevices.viewmodel.BluetoothDevicesViewModel
import com.karlom.bluetoothmessagingapp.feature.shared.SimpleLazyColumn

@Composable
fun BluetoothDevicesScreen(
    viewModel: BluetoothDevicesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val devices = state.devices.collectAsLazyPagingItems()
    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Classic", "BLE")
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
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_left),
                contentDescription = stringResource(R.string.default_icon_content_description),
                modifier = Modifier
                    .padding(4.dp)
                    .clip(CircleShape)
                    .clickable { viewModel.onEvent(OnBackClicked) }
                    .padding(4.dp)
                    .size(30.dp)
            )
            Text(
                text = "Scan",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(text = { Text(title) },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index }
                )
            }
        }
        when (tabIndex) {
            0 -> {
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
                                modifier = Modifier.clickable { },
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
            1 -> {
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
                                modifier = Modifier.clickable { },
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

        }
    }
}

