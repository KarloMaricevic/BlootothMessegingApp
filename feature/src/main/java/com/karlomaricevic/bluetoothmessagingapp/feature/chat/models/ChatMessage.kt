package com.karlomaricevic.bluetoothmessagingapp.feature.chat.models

import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.models.SendMessageStatus

sealed interface ChatItem {

    data class MessageSeparator(
        val id: String,
        val value: Int,
    ) : ChatItem

    data class StartOfMessagingIndicator(val name: String) : ChatItem

    data class DateIndicator(val date: String) : ChatItem

    sealed class ChatMessage(
        open val id: Long,
        open val isFromMe: Boolean,
        open val state: SendMessageStatus,
        open val timestamp: Long,
    ) : ChatItem {
        data class Text(
            override val id: Long,
            val message: String,
            override val isFromMe: Boolean,
            override val state: SendMessageStatus,
            override val timestamp: Long,
        ) : ChatMessage(id, isFromMe, state, timestamp)

        data class Image(
            override val id: Long,
            val imageUri: String,
            val aspectRatio: Float,
            override val isFromMe: Boolean,
            override val state: SendMessageStatus,
            override val timestamp: Long,
        ) : ChatMessage(id, isFromMe, state, timestamp)

        data class Audio(
            override val id: Long,
            val filePath: String,
            val totalTime: String,
            val isPlaying: Boolean = false,
            val currentTime: String? = null,
            override val isFromMe: Boolean,
            override val state: SendMessageStatus,
            override val timestamp: Long,
        ) : ChatMessage(id, isFromMe, state, timestamp)
    }
}
