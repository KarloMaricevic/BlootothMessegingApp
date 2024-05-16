package com.karlom.bluetoothmessagingapp.feature.addDevice

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.karlom.bluetoothmessagingapp.R
import com.karlom.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent.OnDiscoverableSwitchChecked
import com.karlom.bluetoothmessagingapp.feature.addDevice.viewmodel.AddDeviceViewModel

@Composable
fun AddDeviceScreen(
    viewModel: AddDeviceViewModel = hiltViewModel()
) {
    val isDiscoverable by viewModel.isDiscoverableEnabled.collectAsState()
    val makeDiscoverable =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult(),
            onResult = { result ->
                if (result.resultCode > 0)
                    viewModel.onEvent(OnDiscoverableSwitchChecked)
            }
        )
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
                checked = isDiscoverable,
                enabled = !isDiscoverable,
                onCheckedChange = { makeDeviceDiscoverablePermission.launchMultiplePermissionRequest() },
            )
        }
    }
}
