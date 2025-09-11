package com.karlomaricevic.bluetoothmessagingapp.data.messaging.mappers

import com.karlomaricevic.bluetoothmessagingapp.data.db.entites.MessageEntity
import com.karlomaricevic.bluetoothmessagingapp.data.db.entites.MessageType.AUDIO
import com.karlomaricevic.bluetoothmessagingapp.data.db.entites.MessageType.IMAGE
import com.karlomaricevic.bluetoothmessagingapp.data.db.entites.MessageType.TEXT
import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.models.Message.AudioMessage
import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.models.Message.ImageMessage
import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.models.Message.TextMessage

class MessageMapper {

    fun map(entity: MessageEntity) =
        when (entity.messageType) {
            TEXT -> TextMessage(
                id = entity.id,
                message = entity.textContent ?: "",
                isFromMe = entity.isSendByMe,
                state = entity.state,
                timestamp = entity.timestamp,
            )

            IMAGE -> ImageMessage(
                id = entity.id,
                imageUri = entity.filePath ?: "",
                isFromMe = entity.isSendByMe,
                state = entity.state,
                timestamp = entity.timestamp,
            )

            AUDIO -> AudioMessage(
                id = entity.id,
                audioUri = entity.filePath ?: "",
                isFromMe = entity.isSendByMe,
                state = entity.state,
                timestamp = entity.timestamp,
            )
        }
}
