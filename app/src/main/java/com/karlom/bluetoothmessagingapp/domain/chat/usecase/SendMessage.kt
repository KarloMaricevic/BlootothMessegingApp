package com.karlom.bluetoothmessagingapp.domain.chat.usecase

import com.karlom.bluetoothmessagingapp.data.chat.ChatService
import javax.inject.Inject

class SendMessage @Inject constructor(
    private val chatService: ChatService,
) {

    suspend operator fun invoke(message: String) =
        chatService.sendMessage(message)
}
