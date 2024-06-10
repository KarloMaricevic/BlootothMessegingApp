package com.karlom.bluetoothmessagingapp.data.chat

import android.content.Context
import android.provider.OpenableColumns
import androidx.core.net.toUri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import arrow.core.Either
import com.karlom.bluetoothmessagingapp.core.models.Failure
import com.karlom.bluetoothmessagingapp.data.bluetooth.communicationMenager.BluetoothCommunicationManager
import com.karlom.bluetoothmessagingapp.data.bluetooth.models.BluetoothMessage
import com.karlom.bluetoothmessagingapp.data.shared.db.dao.MessageDao
import com.karlom.bluetoothmessagingapp.data.shared.db.enteties.MessageEntity
import com.karlom.bluetoothmessagingapp.data.shared.db.enteties.MessageType
import com.karlom.bluetoothmessagingapp.domain.chat.models.Message
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import java.io.FileNotFoundException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val communicationManager: BluetoothCommunicationManager,
    private val messageDao: MessageDao,
    @ApplicationContext private val context: Context,
) {

    private companion object {
        const val PAGE_SIZE = 20
    }

    suspend fun sendMessage(
        message: String,
        address: String,
    ): Either<Failure.ErrorMessage, Unit> {
        val result = communicationManager.sendText(text = message, address = address)
        result.onRight {
            messageDao.insertAll(
                MessageEntity(
                    isSendByMe = true,
                    textContent = message,
                    filePath = null,
                    messageType = MessageType.TEXT,
                    withContactAddress = address,
                )
            )
        }
        return result
    }

    fun getMessages(withContactAddress: String) = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
        pagingSourceFactory = { messageDao.getMessages(withContactAddress) },
    ).flow.map { page ->
        page.map { entity ->
            when (entity.messageType) {
                MessageType.TEXT -> Message.TextMessage.from(entity)
                MessageType.IMAGE -> Message.ImageMessage.from(entity)
                MessageType.AUDIO -> Message.AudioMessage.from(entity)
            }
        }
    }

    suspend fun sendImage(imageUri: String, address: String): Either<Failure.ErrorMessage, Unit> =
        try {
            val inputImageStream = context.contentResolver.openInputStream(imageUri.toUri())
            val cursor = context.contentResolver.query(imageUri.toUri(), null, null, null, null)
            var result: Either<Failure.ErrorMessage, Unit>? = null
            if (inputImageStream != null && cursor != null) {
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                result = if (sizeIndex != -1 && cursor.moveToFirst()) {
                    communicationManager.sendImage(
                        stream = inputImageStream,
                        streamSize = cursor.getLong(sizeIndex).toInt(),
                        address = address,
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
            when (message) {
                is BluetoothMessage.Text -> {
                    messageDao.insertAll(
                        MessageEntity(
                            isSendByMe = false,
                            textContent = message.text,
                            filePath = null,
                            messageType = MessageType.TEXT,
                            withContactAddress = message.address,
                        )
                    )
                }

                else -> throw NotImplementedError()
            }
        }
    }
}
