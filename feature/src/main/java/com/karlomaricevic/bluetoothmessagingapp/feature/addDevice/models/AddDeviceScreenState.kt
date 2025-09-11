package com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.models

import com.karlomaricevic.bluetoothmessagingapp.domain.connection.models.Connection

data class AddDeviceScreenState(
    val isDiscoverable: Boolean = true,
    val isBluetoothDeviceListShown: Boolean = false,
    val bluetoothDevices: List<Connection>? = null,
    val showMakeDeviceVisibleError: Boolean = false,
    val showConnectingToDeviceError: Boolean = false,
)
