package com.karlomaricevic.bluetoothmessagingapp.domain.connection.usecase

import com.karlomaricevic.bluetoothmessagingapp.domain.connection.ConnectionManager

class ConnectToKnownContact(
    private val manager: ConnectionManager,
) {

    suspend operator fun invoke(address: String) =
        manager.connectToKnownConnection(address)
}
