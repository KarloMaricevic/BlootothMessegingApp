package com.karlom.bluetoothmessagingapp.feature.chat.models

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

data class ChatScreenState(
    val showConnectToDeviceButton: Boolean,
    val isTryingToConnect: Boolean = false,
    val textToSend: String = "",
    val inputMode: ChatInputMode = ChatInputMode.TEXT,
    val isRecordingVoice: Boolean = false,
    val messages: Flow<PagingData<ChatItem>> = flowOf(PagingData.empty()),
)
