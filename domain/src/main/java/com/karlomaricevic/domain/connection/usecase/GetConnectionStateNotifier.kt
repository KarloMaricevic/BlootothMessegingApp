package com.karlomaricevic.domain.connection.usecase

import com.karlomaricevic.domain.connection.ConnectionManager

class ObserveConnectionState(
    private val manager: ConnectionManager
) {

    operator fun invoke() = manager.connectedDevice
}
