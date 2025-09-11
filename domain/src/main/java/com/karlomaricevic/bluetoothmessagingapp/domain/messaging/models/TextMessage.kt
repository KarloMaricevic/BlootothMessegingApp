package com.karlomaricevic.bluetoothmessagingapp.domain.messaging.models

sealed class Message(
    open val id: Long,
    open val isFromMe: Boolean,
    open val state: SendMessageStatus,
    open val timestamp: Long,
) {
    data class TextMessage(
        override val id: Long,
        val message: String,
        override val isFromMe: Boolean,
        override val state: SendMessageStatus,
        override val timestamp: Long,
    ) : Message(id, isFromMe, state, timestamp)

    data class ImageMessage(
        override val id: Long,
        val imageUri: String,
        override val isFromMe: Boolean,
        override val state: SendMessageStatus,
        override val timestamp: Long,
    ) : Message(id, isFromMe, state, timestamp)

    data class AudioMessage(
        override val id: Long,
        val audioUri: String,
        override val isFromMe: Boolean,
        override val state: SendMessageStatus,
        override val timestamp: Long,
    ) : Message(id, isFromMe, state, timestamp)
}
