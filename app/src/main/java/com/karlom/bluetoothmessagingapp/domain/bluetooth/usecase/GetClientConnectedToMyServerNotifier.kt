package com.karlom.bluetoothmessagingapp.domain.bluetooth.usecase

import com.karlom.bluetoothmessagingapp.data.bluetooth.BluetoothConnectionManager
import javax.inject.Inject

class GetClientConnectedToMyServerNotifier @Inject constructor(
    private val connectionManager: BluetoothConnectionManager,
) {

    operator fun invoke() = connectionManager.getClientConnectedMyServerNotifier()
}
