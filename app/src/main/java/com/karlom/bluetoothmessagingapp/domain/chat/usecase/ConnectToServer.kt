package com.karlom.bluetoothmessagingapp.domain.chat.usecase

import com.karlom.bluetoothmessagingapp.data.chat.ChatService
import javax.inject.Inject

class ConnectToServer @Inject constructor(
    private val chatService: ChatService,
) {

    suspend operator fun invoke(address: String) =
        chatService.connectToServer(address)
}
