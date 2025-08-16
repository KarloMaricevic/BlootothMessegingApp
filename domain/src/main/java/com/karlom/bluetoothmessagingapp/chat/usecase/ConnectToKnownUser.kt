package com.karlom.bluetoothmessagingapp.domain.chat.usecase

import com.karlom.bluetoothmessagingapp.data.chat.ChatConnectionManager
import javax.inject.Inject

class ConnectToKnownUser @Inject constructor(
    private val manager: ChatConnectionManager,
) {

    suspend operator fun invoke(address: String) = manager.connectToKnowClient(address)
}
