package com.karlom.bluetoothmessagingapp.data.bluetooth.di

import com.karlom.bluetoothmessagingapp.data.bluetooth.communicationMenager.errorDispatcher.CommunicationErrorDispatcher
import com.karlom.bluetoothmessagingapp.data.bluetooth.communicationMenager.errorDispatcher.CommunicationErrorDispatcherImp
import com.karlom.bluetoothmessagingapp.data.bluetooth.connectionManager.BluetoothConnectionManager
import com.karlom.bluetoothmessagingapp.data.bluetooth.connectionManager.ConnectionNotifier
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface BluetoothModule {

    @Binds
    @Singleton
    fun bindsConnectionNotifier(connectionManager: BluetoothConnectionManager): ConnectionNotifier

    @Binds
    @Singleton
    fun bindsCommunicationErrorDispatcher(errorDispatcher: CommunicationErrorDispatcherImp): CommunicationErrorDispatcher
}
