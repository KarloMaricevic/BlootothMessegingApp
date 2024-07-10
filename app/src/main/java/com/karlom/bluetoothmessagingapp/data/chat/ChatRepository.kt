package com.karlom.bluetoothmessagingapp.data.chat

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import arrow.core.Either
import arrow.core.flatMap
import com.karlom.bluetoothmessagingapp.data.bluetooth.communicationMenager.BluetoothCommunicationManager
import com.karlom.bluetoothmessagingapp.data.bluetooth.models.BluetoothMessage
import com.karlom.bluetoothmessagingapp.data.chat.models.SendState.FINISH_SENDING
import com.karlom.bluetoothmessagingapp.data.chat.models.SendState.SENDING
import com.karlom.bluetoothmessagingapp.data.shared.db.dao.MessageDao
import com.karlom.bluetoothmessagingapp.data.shared.db.enteties.MessageEntity
import com.karlom.bluetoothmessagingapp.data.shared.db.enteties.MessageState
import com.karlom.bluetoothmessagingapp.data.shared.db.enteties.MessageType
import com.karlom.bluetoothmessagingapp.data.shared.interanlStorage.InternalStorage
import com.karlom.bluetoothmessagingapp.domain.chat.models.Message
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

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
    ) = flow {
        var messageEntity = MessageEntity(
            isSendByMe = true,
            textContent = message,
            filePath = null,
            messageType = MessageType.TEXT,
            withContactAddress = address,
            state = MessageState.SENDING,
            timestamp = Date().time
        )
        val id = messageDao.insert(messageEntity)
        messageEntity = messageEntity.copy(id = id)
        emit(SENDING)
        val result = communicationManager.sendText(text = message, address = address)
        result.fold(
            { messageDao.update(messageEntity.copy(state = MessageState.NOT_SENT)) },
            { messageDao.update(messageEntity.copy(state = MessageState.SENT)) },
        )
        emit(FINISH_SENDING)
    }

    fun getMessages(withContactAddress: String) = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
        pagingSourceFactory = { messageDao.loadItemDescending(withContactAddress) }
    ).flow.map { page ->
        page.map { entity ->
            when (entity.messageType) {
                MessageType.TEXT -> Message.TextMessage.from(entity)
                MessageType.IMAGE -> Message.ImageMessage.from(entity)
                MessageType.AUDIO -> Message.AudioMessage.from(entity)
            }
        }
    }

    suspend fun sendImage(imageUri: String, address: String) = flow {
        val savedImagePath = internalStorage.saveImage(
            srcUri = imageUri,
            destName = UUID.randomUUID().toString(),
        )
        val inputStream = savedImagePath.flatMap { imagePath -> internalStorage.getFileInputStream(imagePath) }
        val imageSize = savedImagePath.flatMap { imagePath -> internalStorage.getFileSize(imagePath) }
        if (inputStream is Either.Right && imageSize is Either.Right && savedImagePath is Either.Right) {
            var messageEntity = MessageEntity(
                isSendByMe = true,
                textContent = null,
                filePath = savedImagePath.value,
                messageType = MessageType.IMAGE,
                withContactAddress = address,
                state = MessageState.SENDING,
                timestamp = Date().time,
            )
            val id = messageDao.insert(messageEntity)
            messageEntity = messageEntity.copy(id = id)
            emit(SENDING)
            val result = communicationManager.sendImage(
                stream = inputStream.value,
                streamSize = imageSize.value.toInt(),
                address = address,
            )
            result.fold(
                { _ -> messageDao.update(messageEntity.copy(state = MessageState.NOT_SENT)) },
                { _ -> messageDao.update(messageEntity.copy(state = MessageState.SENT)) },
            )
        }
        inputStream.onRight { stream -> stream.close() }
        emit(FINISH_SENDING)
    }

    suspend fun sendAudio(audioUri: String, address: String) = flow {
        val inputStream = internalStorage.getFileInputStream(audioUri)
        val imageSize = internalStorage.getFileSize(audioUri)
        if (inputStream is Either.Right && imageSize is Either.Right) {
            var messageEntity = MessageEntity(
                isSendByMe = true,
                textContent = null,
                filePath = audioUri,
                messageType = MessageType.AUDIO,
                withContactAddress = address,
                state = MessageState.SENT,
                timestamp = Date().time,
            )
            val id = messageDao.insert(messageEntity)
            messageEntity = messageEntity.copy(id = id)
            emit(SENDING)
            val result = communicationManager.sendAudio(
                stream = inputStream.value,
                streamSize = imageSize.value.toInt(),
                address = address,
            )
            result.fold(
                { _ -> messageDao.update(messageEntity.copy(state = MessageState.NOT_SENT)) },
                { _ -> messageDao.update(messageEntity.copy(state = MessageState.SENT)) },
            )
        }
        inputStream.onRight { stream -> stream.close() }
        emit(FINISH_SENDING)
    }

    suspend fun startSavingReceivedMessages() {
        communicationManager.receivedMessageEvent.collect { message ->
            val timestamp = Date().time
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
                            timestamp = timestamp,
                        )
                    )
                }

                is BluetoothMessage.Image -> {
                    val imagePathResult = internalStorage.save(
                        byteArray = message.image,
                        destName = UUID.randomUUID().toString(),
                    )
                    imagePathResult.onRight { imagePath ->
                        messageDao.insertAll(
                            MessageEntity(
                                isSendByMe = false,
                                textContent = null,
                                filePath = imagePath,
                                messageType = MessageType.IMAGE,
                                withContactAddress = message.address,
                                state = MessageState.SENT,
                                timestamp = timestamp,
                            )
                        )
                    }
                }

                is BluetoothMessage.Audio -> {
                    val audioFilePathResult = internalStorage.save(
                        byteArray = message.audio,
                        destName = UUID.randomUUID().toString()
                    )
                    audioFilePathResult.onRight { imagePath ->
                        messageDao.insertAll(
                            MessageEntity(
                                isSendByMe = false,
                                textContent = null,
                                filePath = imagePath,
                                messageType = MessageType.AUDIO,
                                withContactAddress = message.address,
                                state = MessageState.SENT,
                                timestamp = timestamp,
                            )
                        )
                    }
                }
            }
        }
    }
}
