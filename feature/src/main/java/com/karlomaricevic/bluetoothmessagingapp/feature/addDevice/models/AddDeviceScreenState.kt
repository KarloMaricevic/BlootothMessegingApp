package com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.models

import com.karlomaricevic.bluetoothmessagingapp.domain.connection.models.Connection

data class AddDeviceScreenState(
    val isDiscoverable: Boolean = false,
    val showStartSearchMessage: Boolean = true,
    val bluetoothDevices: List<Connection> = listOf(),
    val showMakeDeviceVisibleError: Boolean = false,
    val showConnectingToDeviceError: Boolean = false,
)
