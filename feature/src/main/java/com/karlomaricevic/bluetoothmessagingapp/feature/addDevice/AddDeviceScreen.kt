package com.karlomaricevic.bluetoothmessagingapp.feature.addDevice

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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.karlomaricevic.bluetoothmessagingapp.feature.R
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.karlomaricevic.bluetoothmessagingapp.designsystem.blue
import com.karlomaricevic.bluetoothmessagingapp.designsystem.gray500
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.components.BluetoothDeviceItem
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent.OnDeviceClicked
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent.OnDiscoverableSwitchChecked
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent.OnScanForDevicesClicked
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.viewmodel.AddDeviceViewModel

@Composable
fun AddDeviceScreen(
    viewModel: AddDeviceViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
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
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
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
                .padding(bottom = 4.dp),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = stringResource(R.string.default_icon_content_description),
                modifier = Modifier
                    .padding(start = 4.dp, bottom = 8.dp)
                    .clip(CircleShape)
                    .clickable { viewModel.onEvent(AddDeviceScreenEvent.OnBackClicked) }
                    .padding(12.dp)
                    .size(16.dp)
            )
            /*Icon(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = stringResource(R.string.default_icon_content_description),
                modifier = Modifier
                    .padding(4.dp)
                    .clip(CircleShape)
                    .clickable { viewModel.onEvent(AddDeviceScreenEvent.OnBackClicked) }
                    .padding(4.dp)
                    .size(25.dp)
            )*/
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
                style = MaterialTheme.typography.titleMedium,
            )
            Switch(
                checked = state.isDiscoverable,
                enabled = !state.isDiscoverable,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(height = 20.dp, width = 16.dp),
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
            modifier = Modifier.padding(start = 8.dp, top = 24.dp)
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
            val devices = state.bluetoothDevices
            if (devices == null) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = stringResource(R.string.bluetooth_device_screen_no_devices_nearby),
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(items = devices, key = { device -> device.address }) { device ->
                        BluetoothDeviceItem(
                            device = device,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.onEvent(OnDeviceClicked(device.address)) }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun isLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}
