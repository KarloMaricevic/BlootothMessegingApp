package com.karlomaricevic.bluetoothmessagingapp.data.messaging

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.karlomaricevic.bluetoothmessagingapp.data.db.MessageQueries
import com.karlomaricevic.bluetoothmessagingapp.data.messaging.mappers.MessageMapper
import com.karlomaricevic.bluetoothmessagingapp.data.messaging.mappers.MessageMapper.Companion.AUDIO_MESSAGE_INDICATOR
import com.karlomaricevic.bluetoothmessagingapp.data.messaging.mappers.MessageMapper.Companion.IMAGE_MESSAGE_INDICATOR
import com.karlomaricevic.bluetoothmessagingapp.data.messaging.mappers.MessageMapper.Companion.ISNT_SENT_BY_ME_INDICATOR
import com.karlomaricevic.bluetoothmessagingapp.data.messaging.mappers.MessageMapper.Companion.IS_SENT_BY_ME_INDICATOR
import com.karlomaricevic.bluetoothmessagingapp.data.messaging.mappers.MessageMapper.Companion.NOT_SENT_INDICATOR
import com.karlomaricevic.bluetoothmessagingapp.data.messaging.mappers.MessageMapper.Companion.SENDING_INDICATOR
import com.karlomaricevic.bluetoothmessagingapp.data.messaging.mappers.MessageMapper.Companion.SENT_INDICATOR
import com.karlomaricevic.bluetoothmessagingapp.data.messaging.mappers.MessageMapper.Companion.TEXT_MESSAGE_INDICATOR
import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.models.SendMessageStatus
import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.models.SendMessageStatus.*
import java.util.Date
import kotlin.collections.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class MessageLocalDataSource(
    private val queries: MessageQueries,
    private val mapper: MessageMapper,
    private val ioDispatcher: CoroutineDispatcher,
) {

    suspend fun saveIncomingTextMessage(text: String, address: String) {
        withContext(ioDispatcher) {
            queries.insertMessage(
                isSendByMe = ISNT_SENT_BY_ME_INDICATOR,
                textContent = text,
                filePath = null,
                messageType = TEXT_MESSAGE_INDICATOR,
                withContactAddress = address,
                state = SENT_INDICATOR,
                timestamp = Date().time
            )
        }
    }

    suspend fun saveIncomingAudioMessage(audioFilePath: String, address: String) {
        withContext(ioDispatcher) {
            queries.insertMessage(
                isSendByMe = ISNT_SENT_BY_ME_INDICATOR,
                textContent = null,
                filePath = audioFilePath,
                messageType = AUDIO_MESSAGE_INDICATOR,
                withContactAddress = address,
                state = SENT_INDICATOR,
                timestamp = Date().time
            )
        }
    }

    suspend fun saveIncomingImageMessage(imageFilePath: String, address: String) {
        withContext(ioDispatcher) {
            queries.insertMessage(
                isSendByMe = ISNT_SENT_BY_ME_INDICATOR,
                textContent = null,
                filePath = imageFilePath,
                messageType = IMAGE_MESSAGE_INDICATOR,
                withContactAddress = address,
                state = SENT_INDICATOR,
                timestamp = Date().time
            )
        }
    }

    suspend fun savePendingTextMessage(text: String, address: String) =
        withContext(ioDispatcher) {
            queries.insertMessage(
                isSendByMe = IS_SENT_BY_ME_INDICATOR,
                textContent = text,
                filePath = null,
                messageType = TEXT_MESSAGE_INDICATOR,
                withContactAddress = address,
                state = SENDING_INDICATOR,
                timestamp = Date().time,
            )
        }

    suspend fun savePendingAudioMessage(audioFilePath: String, address: String) =
        withContext(ioDispatcher) {
            queries.insertMessage(
                isSendByMe = IS_SENT_BY_ME_INDICATOR,
                textContent = null,
                filePath = audioFilePath,
                messageType = AUDIO_MESSAGE_INDICATOR,
                withContactAddress = address,
                state = SENDING_INDICATOR,
                timestamp = Date().time,
            )
        }

     suspend fun savePendingImageMessage(imageFilePath: String, address: String) =
         withContext(ioDispatcher) {
             queries.insertMessage(
                 isSendByMe = IS_SENT_BY_ME_INDICATOR,
                 textContent = null,
                 filePath = imageFilePath,
                 messageType = IMAGE_MESSAGE_INDICATOR,
                 withContactAddress = address,
                 state = SENDING_INDICATOR,
                 timestamp = Date().time,
             )
         }

    suspend fun updateMessageState(id: Long, state: SendMessageStatus) {
        withContext(ioDispatcher) {
            queries.updateMessageState(
                state = when (state) {
                    SENT -> SENT_INDICATOR
                    NOT_SENT -> NOT_SENT_INDICATOR
                    SENDING -> SENDING_INDICATOR
                },
                id = id,
            )
        }
    }

    fun getMessages(withContactAddress: String) =
        queries.selectMessagesForUser(withContactAddress)
            .asFlow()
            .mapToList(ioDispatcher)
            .map { entities -> entities.map { entity -> mapper.map(entity) } }
}
