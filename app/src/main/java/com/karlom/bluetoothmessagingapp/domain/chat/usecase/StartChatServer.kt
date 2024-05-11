package com.karlom.bluetoothmessagingapp.domain.chat.usecase

import com.karlom.bluetoothmessagingapp.data.chat.ChatService
import javax.inject.Inject

class StartChatServer @Inject constructor(
    private val chatService: ChatService,
) {

    operator fun invoke() = chatService.startServer()
}
