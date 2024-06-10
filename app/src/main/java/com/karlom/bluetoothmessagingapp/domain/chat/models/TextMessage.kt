package com.karlom.bluetoothmessagingapp.domain.chat.models

import com.karlom.bluetoothmessagingapp.data.shared.db.enteties.MessageEntity

sealed class Message(
    open val id: Long,
    open val isFromMe: Boolean,
) {
    data class TextMessage(
        override val id: Long,
        val message: String,
        override val isFromMe: Boolean,
    ) : Message(id, isFromMe) {

        companion object {
            fun from(entity: MessageEntity) = TextMessage(
                id = entity.id,
                message = entity.textContent ?: "",
                isFromMe = entity.isSendByMe,
            )
        }
    }

    data class ImageMessage(
        override val id: Long,
        val imageUri: String,
        override val isFromMe: Boolean,
    ) : Message(id, isFromMe) {

        companion object {
            fun from(entity: MessageEntity) = ImageMessage(
                id = entity.id,
                imageUri = entity.filePath ?: "",
                isFromMe = entity.isSendByMe,
            )
        }
    }

    data class AudioMessage(
        override val id: Long,
        val audioUri: String,
        override val isFromMe: Boolean,
    ) : Message(id, isFromMe) {

        companion object {
            fun from(entity: MessageEntity) = AudioMessage(
                id = entity.id,
                audioUri = entity.filePath ?: "",
                isFromMe = entity.isSendByMe,
            )
        }
    }
}
