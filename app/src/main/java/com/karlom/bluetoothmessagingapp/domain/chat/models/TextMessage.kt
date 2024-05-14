package com.karlom.bluetoothmessagingapp.domain.chat.models

import com.karlom.bluetoothmessagingapp.data.shared.db.enteties.MessageEntity

data class TextMessage(
    val id: Long,
    val message: String,
    val isFromMe: Boolean,
) {

    companion object {
        fun from(entity: MessageEntity) = TextMessage(
            id = entity.id,
            message = entity.message,
            isFromMe = entity.isSendByMe,
        )
    }
}
