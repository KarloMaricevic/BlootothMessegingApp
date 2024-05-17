package com.karlom.bluetoothmessagingapp.feature.chat.models

import androidx.paging.PagingData
import com.karlom.bluetoothmessagingapp.domain.chat.models.TextMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

data class ChatScreenState(
    val showConnectToDeviceButton: Boolean,
    val textToSend: String = "",
    val messages: Flow<PagingData<TextMessage>> = flowOf(PagingData.empty()),
)
