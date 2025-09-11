package com.karlomaricevic.bluetoothmessagingapp.app.di

import android.content.Context
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.AppBluetoothManager
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.communicationMenager.BluetoothCommunicationManager
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.connectionManager.BluetoothConnectionClient
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.utils.MessageDecoder
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.utils.MessageEncoder
import com.karlomaricevic.bluetoothmessagingapp.dispatchers.IoDispatcher
import com.karlomaricevic.bluetoothmessagingapp.platform.utils.PermissionChecker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object BluetoothModule {

    @Provides
    @Singleton
    fun provideAppBluetoothManager(@ApplicationContext context: Context) =
        AppBluetoothManager(context)

    @Provides
    @Singleton
    fun provideBluetoothConnectionClient(
        appBtManager: AppBluetoothManager,
        permissionChecker: PermissionChecker,
        @IoDispatcher dispatcher: CoroutineDispatcher,
    ) = BluetoothConnectionClient(
            bluetoothManager = appBtManager,
            permissionChecker = permissionChecker,
            ioDispatcher = dispatcher,
        )

    @Provides
    fun providesEncoder() = MessageEncoder()

    @Provides
    fun providesDecoder() = MessageDecoder()

    @Provides
    @Singleton
    fun providesBluetoothCommunicationManager(
        connectionManager: BluetoothConnectionClient,
        @IoDispatcher dispatcher: CoroutineDispatcher,
        encoder: MessageEncoder,
        decoder: MessageDecoder,
    ) = BluetoothCommunicationManager(
        connectionManager = connectionManager,
        ioDispatcher = dispatcher,
        encoder = encoder,
        decoder = decoder,
    )
}
