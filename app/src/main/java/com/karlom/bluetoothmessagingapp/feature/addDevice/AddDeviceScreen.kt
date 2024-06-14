package com.karlom.bluetoothmessagingapp.feature.addDevice

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.karlom.bluetoothmessagingapp.R
import com.karlom.bluetoothmessagingapp.designSystem.theme.blue
import com.karlom.bluetoothmessagingapp.designSystem.theme.gray500
import com.karlom.bluetoothmessagingapp.feature.addDevice.components.BluetoothDeviceItem
import com.karlom.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent
import com.karlom.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent.OnDeviceClicked
import com.karlom.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent.OnDiscoverableSwitchChecked
import com.karlom.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent.OnScanForDevicesClicked
import com.karlom.bluetoothmessagingapp.feature.addDevice.viewmodel.AddDeviceViewModel
import com.karlom.bluetoothmessagingapp.feature.shared.SimpleLazyColumn

@Composable
fun AddDeviceScreen(
    viewModel: AddDeviceViewModel = hiltViewModel()
) {
    val context = LocalContext.current
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
    val gpsEnabled = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        if (isLocationEnabled(context)) {
            viewModel.onEvent(OnScanForDevicesClicked)
        }
    }
    val enabledBluetooth =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult(),
            onResult = { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S || isLocationEnabled(context)) {
                        viewModel.onEvent(OnScanForDevicesClicked)
                    } else
                        gpsEnabled.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            }
        )
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
            enabledBluetooth.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        }
    }
    Column(Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(top = 2.dp, bottom = 4.dp, start = 8.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = stringResource(R.string.default_icon_content_description),
                modifier = Modifier
                    .padding(4.dp)
                    .clip(CircleShape)
                    .clickable { viewModel.onEvent(AddDeviceScreenEvent.OnBackClicked) }
                    .padding(4.dp)
                    .size(25.dp)
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.End)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(id = R.string.add_device_screen_make_device_visible_message),
                modifier = Modifier.weight(1f, true),
                color = gray500,
                style = MaterialTheme.typography.titleLarge,
            )
            Switch(
                checked = state.isDiscoverable,
                enabled = !state.isDiscoverable,
                onCheckedChange = { makeDeviceDiscoverablePermission.launchMultiplePermissionRequest() },
                colors = SwitchDefaults.colors(
                    disabledCheckedBorderColor = blue,
                    disabledCheckedThumbColor = gray500,
                    disabledCheckedIconColor = blue,
                    disabledCheckedTrackColor = blue,
                ),
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

private fun isLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}
