package com.karlomaricevic.domain.connection.usecase

import com.karlomaricevic.domain.connection.ConnectionManager

class IsConnectedTo(
    private val manager: ConnectionManager
) {

    operator fun invoke(deviceAddress: String) =
        manager.isConnectedToDevice(deviceAddress)
}
