package com.karlomaricevic.domain.connection.usecase

import com.karlomaricevic.domain.connection.ConnectionManager

class ConnectToServer(
    private val manager: ConnectionManager,
) {

    suspend operator fun invoke(address: String) =
        manager.connectToNewConnection(address)
}
