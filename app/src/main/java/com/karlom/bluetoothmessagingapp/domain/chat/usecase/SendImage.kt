package com.karlom.bluetoothmessagingapp.domain.chat.usecase

import com.karlom.bluetoothmessagingapp.data.chat.ChatRepository
import javax.inject.Inject

class SendImage @Inject constructor(
    private val chatRepository: ChatRepository,
) {

    suspend operator fun invoke(imageUri: String) =
        chatRepository.sendImage(imageUri)
}
