package com.karlom.bluetoothmessagingapp.domain.connection.usecase

import com.karlom.bluetoothmessagingapp.data.bluetooth.connectionManager.BluetoothConnectionManager
import javax.inject.Inject

class GetConnectedDevicesNotifier @Inject constructor(
    private val connectionManager: BluetoothConnectionManager
) {

    operator fun invoke() = connectionManager.connectedDevices
}
