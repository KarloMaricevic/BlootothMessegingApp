package com.karlom.bluetoothmessagingapp.domain.chat.usecase

import com.karlom.bluetoothmessagingapp.data.chat.ChatConnectionManager
import javax.inject.Inject

class ConnectToServer @Inject constructor(
    private val connectionManager: ChatConnectionManager,
) {

    suspend operator fun invoke(address: String) =
        connectionManager.connectToServer(address)
}
