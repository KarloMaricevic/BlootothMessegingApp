package com.karlom.bluetoothmessagingapp.domain.chat.usecase

import com.karlom.bluetoothmessagingapp.data.chat.ChatConnectionManager
import javax.inject.Inject

class StartChatServer @Inject constructor(
    private val connectionManager: ChatConnectionManager,
) {

    operator fun invoke() = connectionManager.startServer()
}
