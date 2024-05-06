package com.karlom.bluetoothmessagingapp.domain.chat.models

data class TextMessage(
    val id: Long,
    val message: String,
    val isFromMe: Boolean,
)
