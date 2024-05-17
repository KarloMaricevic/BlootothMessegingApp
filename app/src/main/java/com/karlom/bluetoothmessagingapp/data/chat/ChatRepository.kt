package com.karlom.bluetoothmessagingapp.data.chat

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import arrow.core.Either
import com.karlom.bluetoothmessagingapp.core.models.Failure
import com.karlom.bluetoothmessagingapp.data.bluetooth.BluetoothConnectionManager
import com.karlom.bluetoothmessagingapp.data.shared.db.dao.MessageDao
import com.karlom.bluetoothmessagingapp.data.shared.db.enteties.MessageEntity
import com.karlom.bluetoothmessagingapp.domain.chat.models.TextMessage
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val connectionManager: BluetoothConnectionManager,
    private val messageDao: MessageDao,
) {

    private companion object {
        val CHARSET_UTF_8 = Charsets.UTF_8
        const val PAGE_SIZE = 20
        const val NO_ADDRESS_ERROR = "NO_ADDRESS"
    }

    suspend fun sendMessage(message: String): Either<Failure.ErrorMessage, Unit> {
        val result = connectionManager.send(message.toByteArray(CHARSET_UTF_8))
        result.onRight {
            messageDao.insertAll(
                MessageEntity(
                    isSendByMe = true,
                    message = message,
                    withContactAddress = connectionManager.connectedDeviceAddress
                        ?: NO_ADDRESS_ERROR,
                )
            )
        }
        return result
    }


    fun getMessageReceiver() = connectionManager.getDataReceiverFlow().map { dataFlow ->
        dataFlow
            .map { message -> message.toString(CHARSET_UTF_8) }
            .map { message ->
                messageDao.insertAll(
                    MessageEntity(
                        isSendByMe = false,
                        message = message,
                        withContactAddress = connectionManager.connectedDeviceAddress
                            ?: NO_ADDRESS_ERROR,
                    )
                )
                message
            }
    }

    fun getMessages(withContactAddress: String) = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
        pagingSourceFactory = { messageDao.getMessages(withContactAddress) },
    ).flow.map { page -> page.map { entity -> TextMessage.from(entity) } }
}
