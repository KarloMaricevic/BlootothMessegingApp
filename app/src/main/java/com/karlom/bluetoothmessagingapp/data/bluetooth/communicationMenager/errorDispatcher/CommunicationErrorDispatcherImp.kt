package com.karlom.bluetoothmessagingapp.data.bluetooth.communicationMenager.errorDispatcher

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class CommunicationErrorDispatcherImp @Inject constructor() : CommunicationErrorDispatcher {

    private val _errorEvent = Channel<String>(Channel.BUFFERED)
    override val errorEvent = _errorEvent.receiveAsFlow()

    override suspend fun notify(address: String) {
        _errorEvent.send(address)
    }
}
