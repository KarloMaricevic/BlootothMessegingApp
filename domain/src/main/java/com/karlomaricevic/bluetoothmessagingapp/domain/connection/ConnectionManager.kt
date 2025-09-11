package com.karlomaricevic.bluetoothmessagingapp.domain.connection

import arrow.core.Either
import com.karlomaricevic.bluetoothmessagingapp.core.common.Failure.ErrorMessage
import com.karlomaricevic.bluetoothmessagingapp.domain.connection.models.Connection
import kotlinx.coroutines.flow.Flow

interface ConnectionManager {

    val connectedDevice: Flow<Connection?>
    suspend fun listenForConnection(
        peerIdToAccept: String? = null,
        timeout: Int = -1
    ): Either<ErrorMessage, Connection>

    suspend fun connectToNewConnection(peerId: String): Either<ErrorMessage, Connection>
    suspend fun connectToKnownConnection(peerId: String): Either<ErrorMessage, Connection>

    fun closeConnection()
    fun isConnectedToDevice(peerId: String): Boolean
}
