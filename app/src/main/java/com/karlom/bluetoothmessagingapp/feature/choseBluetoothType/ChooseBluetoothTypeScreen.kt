package com.karlom.bluetoothmessagingapp.feature.choseBluetoothType

import android.Manifest
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.rememberPermissionState
import com.karlom.bluetoothmessagingapp.designSystem.theme.white
import com.karlom.bluetoothmessagingapp.feature.choseBluetoothType.models.ChooseBluetoothTypeScreenEvent.OnSearchBluetoothDevicesClicked
import com.karlom.bluetoothmessagingapp.feature.choseBluetoothType.viewmodel.ChooseBluetoothTypeViewModel
import kotlinx.coroutines.delay

@Composable
fun ChooseBluetoothTypeScreen(
    viewModel: ChooseBluetoothTypeViewModel = hiltViewModel(),
) {
    val discoverableTime = remember { mutableIntStateOf(0) }
    LaunchedEffect(key1 = discoverableTime.intValue) {
        if (discoverableTime.intValue != 0) {
            delay(1000)
            discoverableTime.intValue = --discoverableTime.intValue
        }
    }
    val makeDiscoverable =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult(),
            onResult = { result ->
                if (result.resultCode > 0) {
                    discoverableTime.intValue = result.resultCode
                }
            })
    val makeDeviceDiscoverablePermission = rememberPermissionState(
        permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Manifest.permission.BLUETOOTH_ADVERTISE
        } else {
            Manifest.permission.BLUETOOTH_ADMIN
        }
    ) { permissionAccepted ->
        if (permissionAccepted) {
            makeDiscoverable.launch(Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE))
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Button(
            onClick = { viewModel.onEvent(OnSearchBluetoothDevicesClicked) },
            modifier = Modifier.padding(bottom = 16.dp),
        ) {
            Text(
                text = "Start searching",
                color = white,
            )
        }
        Button(
            onClick = { makeDeviceDiscoverablePermission.launchPermissionRequest() },
            enabled = discoverableTime.intValue == 0
        ) {
            Text(
                text = "Make discoverable",
                color = white,
            )
        }
        if (discoverableTime.intValue != 0) {
            Text(text = "Device is discoverable for ${discoverableTime.intValue}")
        }
    }
}
