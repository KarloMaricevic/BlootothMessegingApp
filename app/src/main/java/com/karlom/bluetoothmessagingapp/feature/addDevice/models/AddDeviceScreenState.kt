package com.karlom.bluetoothmessagingapp.feature.addDevice.models

import com.karlomaricevic.domain.connection.models.Connection

data class AddDeviceScreenState(
    val isDiscoverable: Boolean = true,
    val isBluetoothDeviceListShown: Boolean = false,
    val bluetoothDevices: List<Connection>? = null,
    val showMakeDeviceVisibleError: Boolean = false,
    val showConnectingToDeviceError: Boolean = false,
)
