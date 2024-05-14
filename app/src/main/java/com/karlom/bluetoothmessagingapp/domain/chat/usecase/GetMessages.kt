package com.karlom.bluetoothmessagingapp.domain.chat.usecase

import com.karlom.bluetoothmessagingapp.data.chat.ChatRepository
import javax.inject.Inject

class GetMessages @Inject constructor(
    private val repository: ChatRepository,
) {

    operator fun invoke() = repository.getMessages()
}
