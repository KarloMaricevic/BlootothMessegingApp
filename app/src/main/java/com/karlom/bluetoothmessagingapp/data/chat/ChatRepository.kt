package com.karlom.bluetoothmessagingapp.data.chat

import com.karlom.bluetoothmessagingapp.data.bluetooth.AppBluetoothManager
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val bluetoothManager: AppBluetoothManager,
) {

    private companion object {
        val CHARSET_UTF_8 = Charsets.UTF_8
    }

    suspend fun sendMessage(message: String) =
        bluetoothManager.send(message.toByteArray(CHARSET_UTF_8))

    fun getMessageReceiver() =
        bluetoothManager.getDataReceiverFlow().map { bytes -> bytes.toString(CHARSET_UTF_8) }
}
