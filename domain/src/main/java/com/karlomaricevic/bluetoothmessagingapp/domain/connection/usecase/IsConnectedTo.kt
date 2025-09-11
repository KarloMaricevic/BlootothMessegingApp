package com.karlomaricevic.bluetoothmessagingapp.domain.connection.usecase

import com.karlomaricevic.bluetoothmessagingapp.domain.connection.ConnectionManager

class IsConnectedTo(
    private val manager: ConnectionManager
) {

    operator fun invoke(deviceAddress: String) =
        manager.isConnectedToDevice(deviceAddress)
}
