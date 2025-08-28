package com.karlomaricevic.domain.messaging.usecase

import com.karlomaricevic.domain.messaging.MessageGateway

class StartSavingReceivedMessages(private val gateway: MessageGateway) {

    suspend operator fun invoke() =
        gateway.startSavingIncomingMessages()
}
