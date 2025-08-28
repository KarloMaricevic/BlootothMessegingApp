package com.karlomaricevic.domain.messaging.usecase

import com.karlomaricevic.domain.messaging.MessageGateway

class GetMessages(private val gateway: MessageGateway) {

    operator fun invoke(contactAddress: String) =
        gateway.getMessages(contactAddress)
}
