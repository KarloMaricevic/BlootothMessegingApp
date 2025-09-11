package com.karlomaricevic.bluetoothmessagingapp.domain.messaging.usecase

import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.MessageGateway

class SendAudio(private val gateway: MessageGateway){

    suspend operator fun invoke(imagePath: String, address: String) =
        gateway.sendAudioMessage(audioUri = imagePath, address = address)
}
