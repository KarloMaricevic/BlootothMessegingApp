package com.karlom.bluetoothmessagingapp.domain.connection.usecase

import com.karlom.bluetoothmessagingapp.data.bluetooth.connectionManager.BluetoothConnectionManager
import javax.inject.Inject

class IsConnectedTo @Inject constructor(
    private val connectionManager: BluetoothConnectionManager
) {

    operator fun invoke(deviceAddress: String) =
        connectionManager.isConnectedToDevice(deviceAddress)
}
