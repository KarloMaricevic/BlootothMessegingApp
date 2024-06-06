package com.karlom.bluetoothmessagingapp.data.bluetooth.communicationMenager.errorDispatcher

import kotlinx.coroutines.flow.Flow

interface CommunicationErrorDispatcher {

    val errorEvent: Flow<String>

    suspend fun notify(address: String)
}
