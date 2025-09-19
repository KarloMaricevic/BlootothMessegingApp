package com.karlomaricevic.bluetoothmessagingapp.feature.shared

interface DevicePermissionsHandler {
    fun requestDiscoverable(onResult: (Boolean) -> Unit)
    fun requestScanPermissions(onResult: (Boolean) -> Unit)
    fun enableBluetooth(onResult: (Boolean) -> Unit)
    fun ensureGpsEnabled(onResult: (Boolean) -> Unit)
}
