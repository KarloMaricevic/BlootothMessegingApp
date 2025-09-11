package com.karlomaricevic.bluetoothmessagingapp.domain.messaging

import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.models.Message
import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.models.SendMessageStatus
import kotlinx.coroutines.flow.Flow

interface MessageGateway {
    fun getMessages(withContactAddress: String): Flow<List<Message>>
    suspend fun sendTextMessage(message: String, address: String): Flow<SendMessageStatus>
    suspend fun sendImageMessage(imageUri: String, address: String): Flow<SendMessageStatus>
    suspend fun sendAudioMessage(audioUri: String, address: String): Flow<SendMessageStatus>
    suspend fun startSavingIncomingMessages()
}
