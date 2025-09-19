package com.karlomaricevic.bluetoothmessagingapp.feature.addDevice

import com.karlomaricevic.bluetoothmessagingapp.dispatchers.IoDispatcherTag
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.viewmodel.AddDeviceViewModel
import kotlinx.coroutines.CoroutineScope
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance

val addDeviceScreenModule = DI.Module("AddDeviceScreenModule") {

    bind<AddDeviceViewModel>() with factory { vmScope: CoroutineScope ->
        AddDeviceViewModel(
            observeDiscoverableState = instance(),
            getAvailableConnections = instance(),
            connectToServer = instance(),
            addContact = instance(),
            listenForConnection = instance(),
            navigator = instance(),
            ioDispatcher = instance(tag = IoDispatcherTag),
            vmScope = vmScope,
        )
    }
}
