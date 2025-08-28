package com.karlomaricevic.domain.connection.usecase

import com.karlomaricevic.domain.connection.ConnectionManager

class ConnectToKnownContact(
    private val manager: ConnectionManager,
) {

    suspend operator fun invoke(address: String) =
        manager.connectToKnownConnection(address)
}
