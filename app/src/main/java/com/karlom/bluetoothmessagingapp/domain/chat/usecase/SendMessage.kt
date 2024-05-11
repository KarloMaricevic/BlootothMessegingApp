package com.karlom.bluetoothmessagingapp.domain.chat.usecase

import com.karlom.bluetoothmessagingapp.data.chat.ChatRepository
import javax.inject.Inject

class SendMessage @Inject constructor(
    private val repository: ChatRepository,
) {

    suspend operator fun invoke(message: String) =
        repository.sendMessage(message)
}
