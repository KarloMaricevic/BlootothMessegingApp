package com.karlom.bluetoothmessagingapp.feature.chat.models

sealed class ChatItem(
    open val id: Long,
    open val isFromMe: Boolean,
) {
    data class Text(
        override val id: Long,
        val message: String,
        override val isFromMe: Boolean,
    ) : ChatItem(id, isFromMe)

    data class Image(
        override val id: Long,
        val imageUri: String,
        val aspectRatio: Float,
        override val isFromMe: Boolean,
    ) : ChatItem(id, isFromMe)

    data class Audio(
        override val id: Long,
        val audioUri: String,
        override val isFromMe: Boolean,
    ) : ChatItem(id, isFromMe)
}
