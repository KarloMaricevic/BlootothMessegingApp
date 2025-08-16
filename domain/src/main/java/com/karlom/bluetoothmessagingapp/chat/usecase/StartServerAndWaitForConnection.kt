package com.karlom.bluetoothmessagingapp.domain.chat.usecase

import com.karlom.bluetoothmessagingapp.data.chat.ChatConnectionManager
import javax.inject.Inject

class StartServerAndWaitForConnection @Inject constructor(
    private val connectionManager: ChatConnectionManager
) {

    suspend operator fun invoke() = connectionManager.startServerAndWaitForConnection()
}
