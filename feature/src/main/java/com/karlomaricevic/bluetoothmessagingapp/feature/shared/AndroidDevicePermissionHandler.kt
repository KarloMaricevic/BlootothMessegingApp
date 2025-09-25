package com.karlomaricevic.bluetoothmessagingapp.feature.shared

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
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberAndroidDevicePermissionsHandler(
    context: Context,
    activity: Activity,
): DevicePermissionsHandler {
    val discoverableLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {}
    )
    val bluetoothLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {}
    )
    val gpsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {}
    )

    val discoverablePermissionsState = rememberMultiplePermissionsState(
        permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(Manifest.permission.BLUETOOTH_ADVERTISE, Manifest.permission.BLUETOOTH_SCAN)
        } else listOf(Manifest.permission.BLUETOOTH_ADMIN)
    )

    val scanPermissionsState = rememberMultiplePermissionsState(
        permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(Manifest.permission.BLUETOOTH_SCAN)
        } else listOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val voicePermissionLauncher = rememberPermissionState(permission = Manifest.permission.RECORD_AUDIO)

    return object : DevicePermissionsHandler {

        override fun requestDiscoverable(onResult: (Boolean) -> Unit) {
            if (discoverablePermissionsState.allPermissionsGranted) {
                discoverableLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE))
                onResult(true)
            } else {
                discoverablePermissionsState.launchMultiplePermissionRequest()
            }
        }

        override fun requestScanPermissions(onResult: (Boolean) -> Unit) {
            if (scanPermissionsState.allPermissionsGranted) {
                onResult(true)
            } else {
                scanPermissionsState.launchMultiplePermissionRequest()
            }
        }

        override fun enableBluetooth(onResult: (Boolean) -> Unit) {
            bluetoothLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            onResult(true)
        }

        override fun ensureGpsEnabled(onResult: (Boolean) -> Unit) {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                onResult(true)
            } else {
                gpsLauncher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        }

        override fun requestVoicePermission(onResult: (Boolean) -> Unit) {
            if(voicePermissionLauncher.status == PermissionStatus.Granted) {
                onResult(true)
            } else {
                voicePermissionLauncher.launchPermissionRequest()
            }
        }
    }
}

