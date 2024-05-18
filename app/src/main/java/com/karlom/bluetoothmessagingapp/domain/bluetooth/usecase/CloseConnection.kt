package com.karlom.bluetoothmessagingapp.domain.bluetooth.usecase

import com.karlom.bluetoothmessagingapp.data.bluetooth.BluetoothConnectionManager
import javax.inject.Inject

class CloseConnection @Inject constructor(
    private val connectionManager: BluetoothConnectionManager
) {

    operator fun invoke() = connectionManager.closeConnection()
}
