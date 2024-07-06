package com.karlom.bluetoothmessagingapp.feature.chat.models

import com.karlom.bluetoothmessagingapp.domain.chat.models.MessageState

sealed interface ChatItem {

    data class StartOfMessagingIndicator(val name: String) : ChatItem

    data class DateIndicator(val date: String) : ChatItem

    sealed class ChatMessage(
        open val id: Long,
        open val isFromMe: Boolean,
        open val state: MessageState,
        open val timestamp: Long,
    ) : ChatItem {
        data class Text(
            override val id: Long,
            val message: String,
            override val isFromMe: Boolean,
            override val state: MessageState,
            override val timestamp: Long,
        ) : ChatMessage(id, isFromMe, state, timestamp)

        data class Image(
            override val id: Long,
            val imageUri: String,
            val aspectRatio: Float,
            override val isFromMe: Boolean,
            override val state: MessageState,
            override val timestamp: Long,
        ) : ChatMessage(id, isFromMe, state, timestamp)

        data class Audio(
            override val id: Long,
            val filePath: String,
            val totalTime: String,
            val isPlaying: Boolean = false,
            val currentTime: String? = null,
            override val isFromMe: Boolean,
            override val state: MessageState,
            override val timestamp: Long,
        ) : ChatMessage(id, isFromMe, state, timestamp)
    }
}
