package com.karlomaricevic.bluetoothmessagingapp.app.di.domain

import com.karlomaricevic.bluetoothmessagingapp.domain.connection.ConnectionManager
import com.karlomaricevic.bluetoothmessagingapp.domain.connection.DeviceDiscoverer
import com.karlomaricevic.bluetoothmessagingapp.domain.connection.usecase.CloseConnection
import com.karlomaricevic.bluetoothmessagingapp.domain.connection.usecase.ConnectToKnownContact
import com.karlomaricevic.bluetoothmessagingapp.domain.connection.usecase.ConnectToServer
import com.karlomaricevic.bluetoothmessagingapp.domain.connection.usecase.GetAvailableConnections
import com.karlomaricevic.bluetoothmessagingapp.domain.connection.usecase.IsConnectedTo
import com.karlomaricevic.bluetoothmessagingapp.domain.connection.usecase.ListenForConnection
import com.karlomaricevic.bluetoothmessagingapp.domain.connection.usecase.ObserveConnectionState
import com.karlomaricevic.bluetoothmessagingapp.domain.connection.usecase.ObserveDiscoverableState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object ConnectionModule {

    @Provides
    fun provideObserveDiscoverableState(discoverer: DeviceDiscoverer) =
        ObserveDiscoverableState(discoverer)
    @Provides
    fun providesCloseConnection(manager: ConnectionManager) = CloseConnection(manager)

    @Provides
    fun providesConnectToKnownUser(manager: ConnectionManager) = ConnectToKnownContact(manager)

    @Provides
    fun providesConnectToServer(manager: ConnectionManager) = ConnectToServer(manager)

    @Provides
    fun providesGetAvailableConnections(discoverer: DeviceDiscoverer) =
        GetAvailableConnections(discoverer)

    @Provides
    fun providesListenForConnection(manager: ConnectionManager) =
        ListenForConnection(manager)

    @Provides
    fun providesIsConnectedTo(manager: ConnectionManager) = IsConnectedTo(manager)

    @Provides
    fun provideObserveConnectionState(manager: ConnectionManager) = ObserveConnectionState(manager)
}