package com.karlom.bluetoothmessagingapp.domain.chat.models

import com.karlom.bluetoothmessagingapp.data.shared.db.enteties.MessageEntity
import com.karlom.bluetoothmessagingapp.data.shared.db.enteties.MessageState as MessageStateData

sealed class Message(
    open val id: Long,
    open val isFromMe: Boolean,
    open val state: MessageState,
    open val timestamp: Long,
) {
    data class TextMessage(
        override val id: Long,
        val message: String,
        override val isFromMe: Boolean,
        override val state: MessageState,
        override val timestamp: Long,
    ) : Message(id, isFromMe, state, timestamp) {

        companion object {
            fun from(entity: MessageEntity) = TextMessage(
                id = entity.id,
                message = entity.textContent ?: "",
                isFromMe = entity.isSendByMe,
                state = mapToMessageState(entity.state),
                timestamp = entity.timestamp,
            )
        }
    }

    data class ImageMessage(
        override val id: Long,
        val imageUri: String,
        override val isFromMe: Boolean,
        override val state: MessageState,
        override val timestamp: Long,
    ) : Message(id, isFromMe, state, timestamp) {

        companion object {
            fun from(entity: MessageEntity) = ImageMessage(
                id = entity.id,
                imageUri = entity.filePath ?: "",
                isFromMe = entity.isSendByMe,
                state = mapToMessageState(entity.state),
                timestamp = entity.timestamp,
            )
        }
    }

    data class AudioMessage(
        override val id: Long,
        val audioUri: String,
        override val isFromMe: Boolean,
        override val state: MessageState,
        override val timestamp: Long,
    ) : Message(id, isFromMe, state, timestamp) {

        companion object {
            fun from(entity: MessageEntity) = AudioMessage(
                id = entity.id,
                audioUri = entity.filePath ?: "",
                isFromMe = entity.isSendByMe,
                state = mapToMessageState(entity.state),
                timestamp = entity.timestamp,
            )
        }
    }
}

enum class MessageState {
    SENDING,
    SENT,
    NOT_SENT,
}

private fun mapToMessageState(state: MessageStateData) = when (state) {
    MessageStateData.SENT -> MessageState.SENT
    MessageStateData.NOT_SENT -> MessageState.NOT_SENT
    MessageStateData.SENDING -> MessageState.SENDING
}
