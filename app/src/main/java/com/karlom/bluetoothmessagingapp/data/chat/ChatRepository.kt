package com.karlom.bluetoothmessagingapp.data.chat

import arrow.core.Either
import com.karlom.bluetoothmessagingapp.core.models.Failure
import com.karlom.bluetoothmessagingapp.data.bluetooth.AppBluetoothManager
import com.karlom.bluetoothmessagingapp.data.shared.db.dao.MessageDao
import com.karlom.bluetoothmessagingapp.data.shared.db.enteties.MessageEntity
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val bluetoothManager: AppBluetoothManager,
    private val messageDao: MessageDao,
) {

    private companion object {
        val CHARSET_UTF_8 = Charsets.UTF_8
    }

    suspend fun sendMessage(message: String): Either<Failure.ErrorMessage, Unit> {
        val result = bluetoothManager.send(message.toByteArray(CHARSET_UTF_8))
        result.onRight {
            messageDao.insertAll(
                MessageEntity(
                    isSendByMe = true,
                    message = message,
                )
            )
        }
        return result
    }


    fun getMessageReceiver() =
        bluetoothManager.getDataReceiverFlow()
            .map { bytes -> bytes.toString(CHARSET_UTF_8) }
            .map { message ->
                messageDao.insertAll(
                    MessageEntity(
                        isSendByMe = false,
                        message = message,
                    )
                )
                message
            }
}
