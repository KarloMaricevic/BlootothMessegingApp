package com.karlomaricevic.bluetoothmessagingapp.domain.connection

import com.karlomaricevic.bluetoothmessagingapp.domain.connection.usecase.*
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider


val connectionDomainModule = DI.Module("ConnectionDomainModule") {
    bind<ObserveDiscoverableState>() with provider { ObserveDiscoverableState(instance()) }
    bind<CloseConnection>() with provider { CloseConnection(instance()) }
    bind<ConnectToKnownContact>() with provider { ConnectToKnownContact(instance()) }
    bind<ConnectToServer>() with provider { ConnectToServer(instance()) }
    bind<GetAvailableConnections>() with provider { GetAvailableConnections(instance()) }
    bind<ListenForConnection>() with provider { ListenForConnection(instance()) }
    bind<IsConnectedTo>() with provider { IsConnectedTo(instance()) }
    bind<ObserveConnectionState>() with provider { ObserveConnectionState(instance()) }
}
