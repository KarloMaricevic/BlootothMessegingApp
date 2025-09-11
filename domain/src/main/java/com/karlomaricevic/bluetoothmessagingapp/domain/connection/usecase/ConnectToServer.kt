package com.karlomaricevic.bluetoothmessagingapp.domain.connection.usecase

import com.karlomaricevic.bluetoothmessagingapp.domain.connection.ConnectionManager

class ConnectToServer(
    private val manager: ConnectionManager,
) {

    suspend operator fun invoke(address: String) =
        manager.connectToNewConnection(address)
}
