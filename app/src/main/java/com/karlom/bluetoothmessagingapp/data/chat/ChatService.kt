package com.karlom.bluetoothmessagingapp.data.chat

import com.karlom.bluetoothmessagingapp.data.bluetooth.AppBluetoothManager
import java.util.UUID
import javax.inject.Inject

class ChatService @Inject constructor(
    private val bluetoothManager: AppBluetoothManager,
) {

    private companion object {
        const val SERVICE_NAME = "com.karlom.bluetoohmessagingapp.messageservice"
        const val SERVICE_UUID = "d15a630f-cc8e-482b-a023-89f32e515d40"
    }

    suspend fun startServerAndWaitForConnection() =
        bluetoothManager.startServerAndWaitForConnection(
            serviceName = SERVICE_NAME,
            serviceUUID = UUID.fromString(SERVICE_UUID)
        )

    suspend fun connectToServer(address: String) = bluetoothManager.connectToServer(
        serviceUUID = UUID.fromString(SERVICE_UUID),
        address = address,
    )
}
