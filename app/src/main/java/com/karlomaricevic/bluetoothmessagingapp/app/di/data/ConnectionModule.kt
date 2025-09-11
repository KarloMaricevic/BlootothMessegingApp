package com.karlomaricevic.bluetoothmessagingapp.app.di.data

import com.karlomaricevic.bluetoothmessagingapp.bluetooth.AppBluetoothManager
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.connectionManager.BluetoothConnectionClient
import com.karlomaricevic.bluetoothmessagingapp.data.connection.BluetoothDeviceDiscovererImpl
import com.karlomaricevic.data.connection.ChatConnectionManager
import com.karlomaricevic.bluetoothmessagingapp.domain.connection.ConnectionManager
import com.karlomaricevic.bluetoothmessagingapp.domain.connection.DeviceDiscoverer
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ConnectionModule {

    companion object {

        @Singleton
        @Provides
        fun providesBluetoothDeviceDiscovererImpl(
            manager: AppBluetoothManager,
        ) = BluetoothDeviceDiscovererImpl(manager)

        @Singleton
        @Provides
        fun providesChatConnectionManager(client: BluetoothConnectionClient) =
            ChatConnectionManager(client)
    }

    @Singleton
    @Binds
    fun bindsDeviceDiscoverer(
        bluetoothDeviceDiscovererImpl: BluetoothDeviceDiscovererImpl,
    ): DeviceDiscoverer

    @Singleton
    @Binds
    fun bindsConnectionManager(manager: ChatConnectionManager): ConnectionManager
}
