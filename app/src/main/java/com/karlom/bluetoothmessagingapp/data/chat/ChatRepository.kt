package com.karlom.bluetoothmessagingapp.data.chat

import android.content.Context
import android.provider.OpenableColumns
import androidx.core.net.toUri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import arrow.core.Either
import com.karlom.bluetoothmessagingapp.core.models.Failure
import com.karlom.bluetoothmessagingapp.data.bluetooth.BluetoothCommunicationManager
import com.karlom.bluetoothmessagingapp.data.bluetooth.connectionManager.BluetoothConnectionManager
import com.karlom.bluetoothmessagingapp.data.bluetooth.models.ConnectionState.Connected
import com.karlom.bluetoothmessagingapp.data.shared.db.dao.MessageDao
import com.karlom.bluetoothmessagingapp.data.shared.db.enteties.MessageEntity
import com.karlom.bluetoothmessagingapp.domain.chat.models.TextMessage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import java.io.FileNotFoundException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val communicationManager: BluetoothCommunicationManager,
    private val connectionManager: BluetoothConnectionManager,
    private val messageDao: MessageDao,
    @ApplicationContext private val context: Context,
) {

    private companion object {
        val CHARSET_UTF_8 = Charsets.UTF_8
        const val PAGE_SIZE = 20
        const val NO_ADDRESS_ERROR = "NO_ADDRESS"
    }

    suspend fun sendMessage(message: String): Either<Failure.ErrorMessage, Unit> {
        val result = communicationManager.send(message.toByteArray(CHARSET_UTF_8))
        result.onRight {
            messageDao.insertAll(
                MessageEntity(
                    isSendByMe = true,
                    message = message,
                    withContactAddress = (connectionManager.getConnectionState().value as? Connected)?.device?.address
                        ?: NO_ADDRESS_ERROR,
                )
            )
        }
        return result
    }

    fun getMessages(withContactAddress: String) = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
        pagingSourceFactory = { messageDao.getMessages(withContactAddress) },
    ).flow.map { page -> page.map { entity -> TextMessage.from(entity) } }

    suspend fun sendImage(imageUri: String): Either<Failure.ErrorMessage, Unit> = try {
        val inputImageStream = context.contentResolver.openInputStream(imageUri.toUri())
        val cursor = context.contentResolver.query(imageUri.toUri(), null, null, null, null)
        var result: Either<Failure.ErrorMessage, Unit>? = null
        if (inputImageStream != null && cursor != null) {
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            result = if (sizeIndex != -1 && cursor.moveToFirst()) {
                communicationManager.send(
                    stream = inputImageStream,
                    streamSize = cursor.getLong(sizeIndex),
                )
            } else {
                Either.Left(Failure.ErrorMessage("Error trying to read file size"))
            }
        }
        inputImageStream?.close()
        cursor?.close()
        result ?: Either.Left(Failure.ErrorMessage("Content resolver crashed"))
    } catch (e: FileNotFoundException) {
        Either.Left(Failure.ErrorMessage("Could not open provided uri"))
    }

    suspend fun startSavingReceivedMessages() {
        communicationManager.receivedMessageEvent.collect { message ->
            messageDao.insertAll(
                MessageEntity(
                    isSendByMe = false,
                    message = message.toString(CHARSET_UTF_8),
                    withContactAddress = (connectionManager.getConnectionState().value as? Connected)?.device?.address
                        ?: NO_ADDRESS_ERROR,
                )
            )
        }
    }
}
