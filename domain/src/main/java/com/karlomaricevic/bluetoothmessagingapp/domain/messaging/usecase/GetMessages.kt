package com.karlomaricevic.bluetoothmessagingapp.domain.messaging.usecase

import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.MessageGateway

class GetMessages(private val gateway: MessageGateway) {

    operator fun invoke(contactAddress: String) =
        gateway.getMessages(contactAddress)
}
