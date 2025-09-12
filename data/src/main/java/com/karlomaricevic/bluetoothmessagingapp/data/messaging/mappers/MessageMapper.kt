package com.karlomaricevic.bluetoothmessagingapp.data.messaging.mappers

import com.karlomaricevic.bluetoothmessagingapp.data.db.MessageEntity
import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.models.Message.AudioMessage
import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.models.Message.ImageMessage
import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.models.Message.TextMessage
import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.models.SendMessageStatus

class MessageMapper {

    companion object {
        const val ISNT_SENT_BY_ME_INDICATOR = 0L
        const val IS_SENT_BY_ME_INDICATOR = 1L
        const val TEXT_MESSAGE_INDICATOR = "text"
        const val AUDIO_MESSAGE_INDICATOR = "audio"
        const val IMAGE_MESSAGE_INDICATOR = "image"
        const val SENT_INDICATOR = "sent"
        const val SENDING_INDICATOR = "sending"
        const val NOT_SENT_INDICATOR = "not_sent"
    }

    fun map(entity: MessageEntity) =
        when (entity.messageType) {
            TEXT_MESSAGE_INDICATOR -> TextMessage(
                id = entity.id,
                message = entity.textContent ?: "",
                isFromMe = when(entity.isSendByMe) {
                    ISNT_SENT_BY_ME_INDICATOR -> false
                    IS_SENT_BY_ME_INDICATOR -> true
                    else -> error("Unknown indicator in field isSendByMe in message entity")
                },
                state = when(entity.state) {
                    SENT_INDICATOR -> SendMessageStatus.SENT
                    SENDING_INDICATOR -> SendMessageStatus.SENDING
                    NOT_SENT_INDICATOR -> SendMessageStatus.NOT_SENT
                    else -> error("Unknown indicator in field state in message entity")
                },
                timestamp = entity.timestamp,
            )

            IMAGE_MESSAGE_INDICATOR -> ImageMessage(
                id = entity.id,
                imageUri = entity.filePath ?: "",
                isFromMe = when(entity.isSendByMe) {
                    ISNT_SENT_BY_ME_INDICATOR -> false
                    IS_SENT_BY_ME_INDICATOR -> true
                    else -> error("Unknown indicator in field isSendByMe in message entity")
                },
                state = when(entity.state) {
                    SENT_INDICATOR -> SendMessageStatus.SENT
                    SENDING_INDICATOR -> SendMessageStatus.SENDING
                    NOT_SENT_INDICATOR -> SendMessageStatus.NOT_SENT
                    else -> error("Unknown indicator in field state in message entity")
                },
                timestamp = entity.timestamp,
            )

            AUDIO_MESSAGE_INDICATOR -> AudioMessage(
                id = entity.id,
                audioUri = entity.filePath ?: "",
                isFromMe = when(entity.isSendByMe) {
                    ISNT_SENT_BY_ME_INDICATOR -> false
                    IS_SENT_BY_ME_INDICATOR -> true
                    else -> error("Unknown indicator in field isSendByMe in message entity")
                },
                state = when(entity.state) {
                    SENT_INDICATOR -> SendMessageStatus.SENT
                    SENDING_INDICATOR -> SendMessageStatus.SENDING
                    NOT_SENT_INDICATOR -> SendMessageStatus.NOT_SENT
                    else -> error("Unknown indicator in field state in message entity")
                },
                timestamp = entity.timestamp,
            )
            else -> error("Unknown indicator in field messageType in message entity")
        }
}
