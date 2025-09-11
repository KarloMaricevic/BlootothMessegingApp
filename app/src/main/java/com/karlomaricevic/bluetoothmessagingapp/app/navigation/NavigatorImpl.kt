package com.karlomaricevic.bluetoothmessagingapp.app.navigation

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class NavigatorImpl : Navigator {

    private val _navigationEvent = Channel<NavigationEvent>(Channel.BUFFERED)
    override val navigationEvent = _navigationEvent.receiveAsFlow()

    override suspend fun emitDestination(navigationEvent: NavigationEvent) {
        _navigationEvent.send(navigationEvent)
    }
}
