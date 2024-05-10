package com.karlom.bluetoothmessagingapp.domain.chat.usecase

import com.karlom.bluetoothmessagingapp.data.chat.ChatService
import javax.inject.Inject

class GetReceivedMessages @Inject constructor(
    private val chatService: ChatService,
) {

    operator fun invoke() = chatService.getInputReceiver()
}
