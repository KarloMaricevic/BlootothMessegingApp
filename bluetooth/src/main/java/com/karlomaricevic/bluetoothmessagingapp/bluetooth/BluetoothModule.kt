package com.karlomaricevic.bluetoothmessagingapp.bluetooth

import com.karlomaricevic.bluetoothmessagingapp.bluetooth.communicationMenager.BluetoothCommunicationManager
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.connectionManager.BluetoothConnectionClient
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.utils.MessageDecoder
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.utils.MessageEncoder
import com.karlomaricevic.bluetoothmessagingapp.dispatchers.IoDispatcherTag
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton


val bluetoothModule = DI.Module("BluetoothModule") {

    bind<AppBluetoothManager>() with singleton { AppBluetoothManager(
        context = instance(),
        permissionChecker = instance(),
    ) }

    bind<BluetoothConnectionClient>() with singleton {
        BluetoothConnectionClient(
            bluetoothManager = instance(),
            permissionChecker = instance(),
            ioDispatcher = instance(tag = IoDispatcherTag)
        )
    }

    bind<MessageEncoder>() with singleton { MessageEncoder() }

    bind<MessageDecoder>() with singleton { MessageDecoder() }

    bind<BluetoothCommunicationManager>() with singleton {
        BluetoothCommunicationManager(
            connectionManager = instance(),
            ioDispatcher = instance(tag = IoDispatcherTag),
            encoder = instance(),
            decoder = instance(),
            listeningScope = CoroutineScope(Job() + instance<CoroutineDispatcher>(IoDispatcherTag)),
        )
    }
}
