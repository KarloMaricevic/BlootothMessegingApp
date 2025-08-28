package com.karlomaricevic.domain.connection.usecase

import com.karlomaricevic.domain.connection.ConnectionManager

class ListenForConnection(
    private val manager: ConnectionManager,
) {

    suspend operator fun invoke() = manager.listenForConnection()
}
