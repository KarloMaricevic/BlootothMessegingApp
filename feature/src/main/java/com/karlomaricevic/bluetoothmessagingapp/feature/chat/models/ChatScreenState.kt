package com.karlomaricevic.bluetoothmessagingapp.feature.chat.models

data class ChatScreenState(
    val showConnectToDeviceButton: Boolean,
    val isTryingToConnect: Boolean = false,
    val textToSend: String = "",
    val inputMode: ChatInputMode = ChatInputMode.TEXT,
    val isRecordingVoice: Boolean = false,
    val messages: List<ChatItem> = listOf(),
)
