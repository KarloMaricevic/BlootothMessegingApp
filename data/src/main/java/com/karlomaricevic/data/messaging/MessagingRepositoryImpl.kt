package com.karlomaricevic.data.messaging

import arrow.core.Either
import arrow.core.flatMap
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.communicationMenager.BluetoothCommunicationManager
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.models.TransportMessage.Audio
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.models.TransportMessage.Image
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.models.TransportMessage.Text
import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.MessageGateway
import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.models.SendMessageStatus.NOT_SENT
import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.models.SendMessageStatus.SENDING
import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.models.SendMessageStatus.SENT
import com.karlomaricevic.platform.utils.FileStorage
import java.util.UUID
import kotlinx.coroutines.flow.flow

class MessagingRepositoryImpl(
    private val communicationManager: BluetoothCommunicationManager,
    private val localMessages: MessageLocalDataSource,
    private val internalStorage: FileStorage,
) : MessageGateway {

    override suspend fun sendTextMessage(
        message: String,
        address: String,
    ) = flow {
        val savedMessage = localMessages.savePendingTextMessage(
            text = message,
            address = address,
        )
        emit(SENDING)
        val result = communicationManager.sendText(message)
        result.fold(
            { _ -> localMessages.updateMessageState(id = savedMessage.id, state = NOT_SENT) },
            { localMessages.updateMessageState(id = savedMessage.id, state = SENT) },
        )
    }

    override fun getMessages(withContactAddress: String) =
        localMessages.getMessages(withContactAddress)

    override suspend fun sendImageMessage(imageUri: String, address: String) = flow {
        val savedImagePath = internalStorage.saveImage(
            filePath = imageUri,
            destName = UUID.randomUUID().toString(),
        )
        val inputStream = savedImagePath.flatMap { imagePath -> internalStorage.getFileInputStream(imagePath) }
        val imageSize = savedImagePath.flatMap { imagePath -> internalStorage.getFileSize(imagePath) }
        if (inputStream is Either.Right && imageSize is Either.Right && savedImagePath is Either.Right) {
            val savedMessage = localMessages.savePendingImageMessage(
                imageFilePath = savedImagePath.toString(),
                address = address,
            )
            emit(SENDING)
            val result = communicationManager.sendImage(
                stream = inputStream.value,
                size = imageSize.value.toInt(),
            )
            result.fold(
                { _ -> localMessages.updateMessageState(id = savedMessage.id, state = NOT_SENT) },
                { _ -> localMessages.updateMessageState(id = savedMessage.id, state = SENT) },
            )
        }
        inputStream.onRight { stream -> stream.close() }
    }

    override suspend fun sendAudioMessage(audioUri: String, address: String) = flow {
        val inputStream = internalStorage.getFileInputStream(audioUri)
        val imageSize = internalStorage.getFileSize(audioUri)
        if (inputStream is Either.Right && imageSize is Either.Right) {
            val savedMessage = localMessages.savePendingAudioMessage(
                audioFilePath = audioUri,
                address = address,
            )
            emit(SENDING)
            val result = communicationManager.sendAudio(
                stream = inputStream.value,
                size = imageSize.value.toInt(),
            )
            result.fold(
                { _ -> localMessages.updateMessageState(id = savedMessage.id, state = NOT_SENT) },
                { _ -> localMessages.updateMessageState(id = savedMessage.id, state = SENT) },
            )
        }
        inputStream.onRight { stream -> stream.close() }
    }

    override suspend fun startSavingIncomingMessages() {
        communicationManager.receivedMessageEvent.collect { message ->
            when (message) {
                is Text -> {
                    localMessages.saveIncomingTextMessage(message.text, message.address)
                }

                is Image -> {
                    val imagePathResult = internalStorage.save(
                        byteArray = message.image,
                        destName = UUID.randomUUID().toString(),
                    )
                    imagePathResult.onRight { imageFilePath ->
                        localMessages.saveIncomingImageMessage(
                            imageFilePath = imageFilePath,
                            address = message.address,
                        )
                    }
                }

                is Audio -> {
                    val audioFilePathResult = internalStorage.save(
                        byteArray = message.audio,
                        destName = UUID.randomUUID().toString()
                    )
                    audioFilePathResult.onRight { audioFilePath ->
                        localMessages.savePendingAudioMessage(
                            audioFilePath = audioFilePath,
                            address = message.address,
                        )
                    }
                }
            }
        }
    }
}
