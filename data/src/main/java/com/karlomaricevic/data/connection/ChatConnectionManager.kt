package com.karlomaricevic.data.connection

import com.karlomaricevic.bluetooth.connectionManager.BluetoothConnectionClient
import com.karlomaricevic.domain.connection.ConnectionManager
import com.karlomaricevic.domain.connection.models.Connection
import kotlinx.coroutines.flow.Flow

class ChatConnectionManager(
    private val manager: BluetoothConnectionClient,
) : ConnectionManager {
    private companion object {
        const val SERVICE_NAME = "com.karlom.bluetoohmessagingapp.messageservice"
        const val SERVICE_UUID = "d15a630f-cc8e-482b-a023-89f32e515d40"
    }

    override val connectedDevice: Flow<Connection?> = manager.connectedDevice

    override suspend fun listenForConnection(
        peerIdToAccept: String?,
        timeout: Int,
    ) = manager.startServerAndWaitForConnection(
        serviceName = SERVICE_NAME,
        serviceUUID = SERVICE_UUID,
        clientAddress = peerIdToAccept,
        timeout = timeout,
    )

    override suspend fun connectToNewConnection(peerId: String) =
        manager.connectToServer(
            serviceUUID = SERVICE_UUID,
            address = peerId,
        )

    override suspend fun connectToKnownConnection(peerId: String) =
        manager.connectToKnownDevice(
            serviceName = SERVICE_NAME,
            serviceUUID = SERVICE_UUID,
            address = peerId,
        )

    override fun closeConnection() {
        manager.closeConnection()
    }

    override fun isConnectedToDevice(peerId: String) =
        manager.isConnectedToDevice(peerId)
}
