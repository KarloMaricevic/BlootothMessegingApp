package com.karlom.bluetoothmessagingapp.data.chat

import com.karlom.bluetoothmessagingapp.data.bluetooth.AppBluetoothManager
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class ChatService @Inject constructor(
    private val bluetoothManager: AppBluetoothManager,
) {

    private companion object {
        const val SERVICE_NAME = "com.karlom.bluetoohmessagingapp.messageservice"
        const val SERVICE_UUID = "d15a630f-cc8e-482b-a023-89f32e515d40"
        val CHARSET_UTF_8 = Charsets.UTF_8
    }

    suspend fun startServer() = bluetoothManager.startServer(
        serviceName = SERVICE_NAME,
        serviceUUID = UUID.fromString(SERVICE_UUID),
    )

    suspend fun connectToServer(address: String) = bluetoothManager.connectToServer(
        serviceUUID = UUID.fromString(SERVICE_UUID),
        address = address,
    )

    suspend fun sendMessage(message: String) =
        bluetoothManager.send(message.toByteArray(CHARSET_UTF_8))

    fun getInputReceiver() =
        bluetoothManager.getDataReceiverFlow().map { bytes -> bytes.toString(CHARSET_UTF_8) }
}
