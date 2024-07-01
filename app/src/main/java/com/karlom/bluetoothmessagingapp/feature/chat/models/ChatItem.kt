package com.karlom.bluetoothmessagingapp.feature.chat.models

import com.karlom.bluetoothmessagingapp.domain.chat.models.MessageState

sealed class ChatItem(
    open val id: Long,
    open val isFromMe: Boolean,
    open val state: MessageState,
) {
    data class Text(
        override val id: Long,
        val message: String,
        override val isFromMe: Boolean,
        override val state: MessageState,
    ) : ChatItem(id, isFromMe, state)

    data class Image(
        override val id: Long,
        val imageUri: String,
        val aspectRatio: Float,
        override val isFromMe: Boolean,
        override val state: MessageState,
    ) : ChatItem(id, isFromMe, state)

    data class Audio(
        override val id: Long,
        val filePath: String,
        val totalTime: String,
        val isPlaying: Boolean = false,
        val currentTime: String? = null,
        override val isFromMe: Boolean,
        override val state: MessageState,
    ) : ChatItem(id, isFromMe, state)
}
