package com.karlom.bluetoothmessagingapp.domain.chat.usecase

import android.location.Address
import com.karlom.bluetoothmessagingapp.data.chat.ChatRepository
import javax.inject.Inject

class SendImage @Inject constructor(
    private val chatRepository: ChatRepository,
) {

    suspend operator fun invoke(imageUri: String, address: String) =
        chatRepository.sendImage(imageUri = imageUri, address = address)
}
