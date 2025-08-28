package com.karlomaricevic.domain.connection.usecase

import com.karlomaricevic.domain.connection.ConnectionManager

class CloseConnection(
    private val manager: ConnectionManager,
) {

    operator fun invoke() = manager.closeConnection()
}
