package com.karlomaricevic.bluetoothmessagingapp.domain.messaging.usecase

import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.MessageGateway

class SendText(private val gateway: MessageGateway) {

    suspend operator fun invoke(message: String, address: String) =
        gateway.sendTextMessage(message = message, address = address)
}
