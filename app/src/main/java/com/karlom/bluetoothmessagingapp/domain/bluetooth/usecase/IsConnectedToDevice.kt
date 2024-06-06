package com.karlom.bluetoothmessagingapp.domain.bluetooth.usecase

import com.karlom.bluetoothmessagingapp.data.bluetooth.connectionManager.BluetoothConnectionManager
import javax.inject.Inject

class IsConnectedToDevice @Inject constructor(
    private val connectionManager: BluetoothConnectionManager
) {

    operator fun invoke(deviceAddress: String) =
        connectionManager.isConnectedToDevice(deviceAddress)
}
