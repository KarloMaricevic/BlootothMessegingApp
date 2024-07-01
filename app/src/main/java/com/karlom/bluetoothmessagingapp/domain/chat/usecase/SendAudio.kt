package com.karlom.bluetoothmessagingapp.domain.chat.usecase

import com.karlom.bluetoothmessagingapp.data.chat.ChatRepository
import javax.inject.Inject

class SendAudio @Inject constructor(
    private val chatRepository: ChatRepository,
) {

    suspend operator fun invoke(imagePath: String, address: String) =
        chatRepository.sendAudio(audioUri = imagePath, address = address)
}
