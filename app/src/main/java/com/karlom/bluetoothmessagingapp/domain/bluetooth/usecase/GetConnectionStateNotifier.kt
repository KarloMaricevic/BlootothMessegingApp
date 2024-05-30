package com.karlom.bluetoothmessagingapp.domain.bluetooth.usecase

import com.karlom.bluetoothmessagingapp.data.bluetooth.connectionManager.BluetoothConnectionManager
import javax.inject.Inject

class GetConnectionStateNotifier @Inject constructor(
    private val connectionManager: BluetoothConnectionManager
) {

    operator fun invoke() = connectionManager.getConnectionState()
}
