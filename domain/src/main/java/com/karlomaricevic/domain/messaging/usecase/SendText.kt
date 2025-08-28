package com.karlomaricevic.domain.messaging.usecase

import com.karlomaricevic.domain.messaging.MessageGateway

class SendText(private val gateway: MessageGateway) {

    suspend operator fun invoke(message: String, address: String) =
        gateway.sendTextMessage(message = message, address = address)
}
