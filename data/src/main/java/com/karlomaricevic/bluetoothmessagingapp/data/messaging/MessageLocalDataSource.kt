package com.karlomaricevic.bluetoothmessagingapp.data.messaging

import com.karlomaricevic.bluetoothmessagingapp.data.db.daos.MessageDao
import com.karlomaricevic.bluetoothmessagingapp.data.db.entites.MessageEntity
import com.karlomaricevic.bluetoothmessagingapp.data.db.entites.MessageType.AUDIO
import com.karlomaricevic.bluetoothmessagingapp.data.db.entites.MessageType.IMAGE
import com.karlomaricevic.bluetoothmessagingapp.data.db.entites.MessageType.TEXT
import com.karlomaricevic.bluetoothmessagingapp.data.messaging.mappers.MessageMapper
import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.models.Message
import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.models.SendMessageStatus
import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.models.SendMessageStatus.SENDING
import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.models.SendMessageStatus.SENT
import java.util.Date
import kotlin.collections.map
import kotlinx.coroutines.flow.map

class MessageLocalDataSource(
    private val dao: MessageDao,
    private val mapper: MessageMapper,
) {

    suspend fun saveIncomingTextMessage(text: String, address: String): Message {
        val entity = MessageEntity(
            isSendByMe = false,
            textContent = text,
            filePath = null,
            messageType = TEXT,
            withContactAddress = address,
            state = SENT,
            timestamp = Date().time
        )
        val id = dao.insert(entity)
        return mapper.map(entity.copy(id = id))
    }


    suspend fun saveIncomingAudioMessage(audioFilePath: String, address: String): Message {
        val entity = MessageEntity(
            isSendByMe = false,
            textContent = null,
            filePath = audioFilePath,
            messageType = AUDIO,
            withContactAddress = address,
            state = SENT,
            timestamp = Date().time
        )
        val id = dao.insert(entity)
        return mapper.map(entity.copy(id = id))
    }

    suspend fun saveIncomingImageMessage(imageFilePath: String, address: String): Message {
        val entity = MessageEntity(
            isSendByMe = false,
            textContent = null,
            filePath = imageFilePath,
            messageType = IMAGE,
            withContactAddress = address,
            state = SENT,
            timestamp = Date().time
        )
        val id = dao.insert(entity)
        return mapper.map(entity.copy(id = id))
    }

    suspend fun savePendingTextMessage(text: String, address: String): Message {
        val entity = MessageEntity(
            isSendByMe = true,
            textContent = text,
            filePath = null,
            messageType = TEXT,
            withContactAddress = address,
            state = SENDING,
            timestamp = Date().time,
        )
        val id = dao.insert(entity)
        return mapper.map(entity.copy(id = id))
    }

    suspend fun savePendingAudioMessage(audioFilePath: String, address: String): Message {
        val entity = MessageEntity(
            isSendByMe = true,
            textContent = null,
            filePath = audioFilePath,
            messageType = AUDIO,
            withContactAddress = address,
            state = SENDING,
            timestamp = Date().time,
        )
        val id = dao.insert(entity)
        return mapper.map(entity.copy(id = id))
    }

    suspend fun savePendingImageMessage(imageFilePath: String, address: String): Message {
        val entity = MessageEntity(
            isSendByMe = true,
            textContent = null,
            filePath = imageFilePath,
            messageType = IMAGE,
            withContactAddress = address,
            state = SENDING,
            timestamp = Date().time,
        )
        val id = dao.insert(entity)
        return mapper.map(entity.copy(id = id))
    }

    suspend fun updateMessageState(id: Long, state: SendMessageStatus) =
        dao.updateStateById(id, state)

    fun getMessages(withContactAddress: String) =
        dao.loadAllMessagesForUser(withContactAddress).map { messages ->
            messages.map { message -> mapper.map(message) }
        }
}
