package com.karlom.bluetoothmessagingapp.feature.chat.models

import com.karlom.bluetoothmessagingapp.domain.chat.models.TextMessage

data class ChatScreenState(
    val textToSend: String = "",
    val messages: List<TextMessage> = listOf(),
)
