package com.karlomaricevic.app2.di.data

import com.karlomaricevic.bluetooth.AppBluetoothManager
import com.karlomaricevic.bluetooth.connectionManager.BluetoothConnectionClient
import com.karlomaricevic.data.connection.BluetoothDeviceDiscovererImpl
import com.karlomaricevic.data.connection.ChatConnectionManager
import com.karlomaricevic.domain.connection.ConnectionManager
import com.karlomaricevic.domain.connection.DeviceDiscoverer
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
