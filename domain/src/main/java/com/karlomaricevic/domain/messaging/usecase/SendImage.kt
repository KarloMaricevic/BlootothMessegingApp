package com.karlomaricevic.domain.messaging.usecase

import com.karlomaricevic.domain.messaging.MessageGateway

class SendImage(private val gateway: MessageGateway) {

    suspend operator fun invoke(imageUri: String, address: String) =
        gateway.sendImageMessage(imageUri = imageUri, address = address)
}
