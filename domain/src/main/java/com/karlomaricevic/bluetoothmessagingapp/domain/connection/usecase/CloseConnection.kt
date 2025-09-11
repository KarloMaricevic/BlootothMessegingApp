package com.karlomaricevic.bluetoothmessagingapp.domain.connection.usecase

import com.karlomaricevic.bluetoothmessagingapp.domain.connection.ConnectionManager

class CloseConnection(
    private val manager: ConnectionManager,
) {

    operator fun invoke() = manager.closeConnection()
}
