package com.karlom.bluetoothmessagingapp.data.chat

import android.content.Context
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
import com.karlom.bluetoothmessagingapp.data.shared.interanlStorage.InternalStorage
import com.karlom.bluetoothmessagingapp.domain.chat.models.Message
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val communicationManager: BluetoothCommunicationManager,
    private val internalStorage: InternalStorage,
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

    suspend fun sendImage(imageUri: String, address: String): Either<Failure.ErrorMessage, Unit> {
        val savedImageUri = internalStorage.saveImage(imageUri, UUID.randomUUID().toString())
        val inputStream = internalStorage.getFileInputStream(imageUri)
        val imageSize = internalStorage.getFileSize(imageUri)
        return if (inputStream is Either.Right && imageSize is Either.Right && savedImageUri is Either.Right) {
            val result =
                communicationManager.sendImage(
                    stream = inputStream.value,
                    streamSize = imageSize.value.toInt(),
                    address = address,
                )
            inputStream.value.close()
            result.onRight {
                messageDao.insertAll(
                    MessageEntity(
                        isSendByMe = true,
                        textContent = null,
                        filePath = savedImageUri.value,
                        messageType = MessageType.IMAGE,
                        withContactAddress = address,
                    )
                )
            }
            Either.Right(Unit)
        } else {
            inputStream.onRight { it.close() }
            listOf(
                savedImageUri,
                inputStream,
                imageSize,
            ).firstOrNull { it is Either.Left } as? Either.Left ?: Either.Left(
                Failure.ErrorMessage("Error sending image")
            )
        }
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

                is BluetoothMessage.Image -> {
                    val imagePathResult =
                        internalStorage.save(message.image, UUID.randomUUID().toString())
                    imagePathResult.onRight { imagePath ->
                        messageDao.insertAll(
                            MessageEntity(
                                isSendByMe = false,
                                textContent = null,
                                filePath = imagePath,
                                messageType = MessageType.IMAGE,
                                withContactAddress = message.address,
                            )
                        )
                    }
                }

                else -> throw NotImplementedError()
            }
        }
    }
}
