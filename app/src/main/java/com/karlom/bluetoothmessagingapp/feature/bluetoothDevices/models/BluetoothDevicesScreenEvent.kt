package com.karlom.bluetoothmessagingapp.feature.bluetoothDevices.models

sealed interface BluetoothDevicesScreenEvent {

    data object OnScanForDevicesClicked : BluetoothDevicesScreenEvent
}
