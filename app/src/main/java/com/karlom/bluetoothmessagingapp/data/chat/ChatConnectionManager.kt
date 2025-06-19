package com.karlom.bluetoothmessagingapp.data.chat

import com.karlom.bluetoothmessagingapp.data.bluetooth.connectionManager.BluetoothConnectionManager
import java.util.UUID
import javax.inject.Inject

class ChatConnectionManager @Inject constructor(
    private val connectionManager: BluetoothConnectionManager,
) {
    private companion object {
        const val SERVICE_NAME = "com.karlom.bluetoohmessagingapp.messageservice"
        const val SERVICE_UUID = "d15a630f-cc8e-482b-a023-89f32e515d40"
    }

    suspend fun connectToServer(address: String) = connectionManager.connectToServer(
        serviceUUID = UUID.fromString(SERVICE_UUID),
        address = address,
    )

    suspend fun startServerAndWaitForConnection() =
        connectionManager.startServerAndWaitForConnection(
            serviceName = SERVICE_NAME,
            serviceUUID = UUID.fromString(SERVICE_UUID),
        )

    suspend fun connectToKnowClient(address: String) = connectionManager.connectToKnownDevice(
        serviceName = SERVICE_NAME,
        serviceUUID = UUID.fromString(SERVICE_UUID),
        address = address,
    )
}
