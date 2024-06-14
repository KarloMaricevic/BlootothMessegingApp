package com.karlom.bluetoothmessagingapp.data.chat

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import arrow.core.Either
import com.karlom.bluetoothmessagingapp.core.models.Failure
import com.karlom.bluetoothmessagingapp.data.bluetooth.communicationMenager.BluetoothCommunicationManager
import com.karlom.bluetoothmessagingapp.data.bluetooth.models.BluetoothMessage
import com.karlom.bluetoothmessagingapp.data.shared.db.dao.MessageDao
import com.karlom.bluetoothmessagingapp.data.shared.db.enteties.MessageEntity
import com.karlom.bluetoothmessagingapp.data.shared.db.enteties.MessageState
import com.karlom.bluetoothmessagingapp.data.shared.db.enteties.MessageType
import com.karlom.bluetoothmessagingapp.data.shared.interanlStorage.InternalStorage
import com.karlom.bluetoothmessagingapp.domain.chat.models.Message
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val communicationManager: BluetoothCommunicationManager,
    private val internalStorage: InternalStorage,
    private val messageDao: MessageDao,
) {

    private companion object {
        const val PAGE_SIZE = 20
    }

    suspend fun sendMessage(
        message: String,
        address: String,
    ): Either<Failure.ErrorMessage, Unit> {
        var messageEntity = MessageEntity(
            isSendByMe = true,
            textContent = message,
            filePath = null,
            messageType = MessageType.TEXT,
            withContactAddress = address,
            state = MessageState.SENDING,
        )
        val id = messageDao.insert(messageEntity)
        messageEntity = messageEntity.copy(id = id)
        val result = communicationManager.sendText(text = message, address = address)
        result.onRight { messageDao.update(messageEntity.copy(state = MessageState.SENT)) }
        result.onLeft { messageDao.update(messageEntity.copy(state = MessageState.NOT_SENT)) }
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
        val savedImagePath = internalStorage.saveImage(imageUri, UUID.randomUUID().toString())
        val inputStream = when (savedImagePath) {
            is Either.Left -> Either.Left(Failure.ErrorMessage("No image path"))
            is Either.Right -> internalStorage.getFileInputStream(savedImagePath.value)
        }
        val imageSize = when (savedImagePath) {
            is Either.Left -> Either.Left(Failure.ErrorMessage("No image path"))
            is Either.Right -> internalStorage.getFileSize(savedImagePath.value)
        }
        internalStorage.getFileSize(imageUri)
        return if (inputStream is Either.Right && imageSize is Either.Right && savedImagePath is Either.Right) {
            var messageEntity = MessageEntity(
                isSendByMe = true,
                textContent = null,
                filePath = savedImagePath.value,
                messageType = MessageType.IMAGE,
                withContactAddress = address,
                state = MessageState.SENDING,
            )
            val id = messageDao.insert(messageEntity)
            messageEntity = messageEntity.copy(id = id)
            val result =
                communicationManager.sendImage(
                    stream = inputStream.value,
                    streamSize = imageSize.value.toInt(),
                    address = address,
                )
            inputStream.value.close()
            result.onRight { messageDao.update(messageEntity.copy(state = MessageState.SENT)) }
            result.onLeft { messageDao.update(messageEntity.copy(state = MessageState.NOT_SENT)) }
            return result
        } else {
            inputStream.onRight { it.close() }
            listOf(
                savedImagePath,
                inputStream,
                imageSize,
            ).firstOrNull { it is Either.Left } as? Either.Left ?: Either.Left(
                Failure.ErrorMessage("Error sending image")
            )
        }
    }

    suspend fun sendAudio(audioUri: String, address: String): Either<Failure.ErrorMessage, Unit> {
        val inputStream = internalStorage.getFileInputStream(audioUri)
        val imageSize = internalStorage.getFileSize(audioUri)
        if (inputStream is Either.Right && imageSize is Either.Right) {
            var messageEntity = MessageEntity(
                isSendByMe = true,
                textContent = null,
                filePath = audioUri,
                messageType = MessageType.IMAGE,
                withContactAddress = address,
                state = MessageState.SENT,
            )
            val id = messageDao.insert(messageEntity)
            messageEntity = messageEntity.copy(id = id)
            val result = communicationManager.sendAudio(
                stream = inputStream.value,
                streamSize = imageSize.value.toInt(),
                address = address,
            )
            inputStream.onRight { messageDao.update(messageEntity.copy(state = MessageState.SENT)) }
            inputStream.onLeft { messageDao.update(messageEntity.copy(state = MessageState.NOT_SENT)) }
            inputStream.value.close()
            return result
        }
        inputStream.onRight { it.close() }
        return listOf(
            inputStream,
            imageSize,
        ).firstOrNull { it is Either.Left } as? Either.Left ?: Either.Left(
            Failure.ErrorMessage("Error sending audio")
        )
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
                            state = MessageState.SENT,
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
                                state = MessageState.SENT,
                            )
                        )
                    }
                }

                is BluetoothMessage.Audio -> {
                    val audioFilePathResult =
                        internalStorage.save(message.audio, UUID.randomUUID().toString())
                    audioFilePathResult.onRight { imagePath ->
                        messageDao.insertAll(
                            MessageEntity(
                                isSendByMe = false,
                                textContent = null,
                                filePath = imagePath,
                                messageType = MessageType.AUDIO,
                                withContactAddress = message.address,
                                state = MessageState.SENT,
                            )
                        )
                    }
                }
            }
        }
    }
}
