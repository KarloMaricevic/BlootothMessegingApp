package com.karlomaricevic.domain.messaging.usecase

import com.karlomaricevic.domain.messaging.MessageGateway

class SendAudio(private val gateway: MessageGateway){

    suspend operator fun invoke(imagePath: String, address: String) =
        gateway.sendAudioMessage(audioUri = imagePath, address = address)
}
