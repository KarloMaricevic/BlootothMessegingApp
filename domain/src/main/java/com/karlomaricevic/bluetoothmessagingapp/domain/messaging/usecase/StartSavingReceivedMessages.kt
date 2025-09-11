package com.karlomaricevic.bluetoothmessagingapp.domain.messaging.usecase

import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.MessageGateway

class StartSavingReceivedMessages(private val gateway: MessageGateway) {

    suspend operator fun invoke() =
        gateway.startSavingIncomingMessages()
}
