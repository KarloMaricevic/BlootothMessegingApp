package com.karlomaricevic.bluetoothmessagingapp.domain.connection.usecase

import com.karlomaricevic.bluetoothmessagingapp.domain.connection.ConnectionManager

class ListenForConnection(
    private val manager: ConnectionManager,
) {

    suspend operator fun invoke() = manager.listenForConnection()
}
