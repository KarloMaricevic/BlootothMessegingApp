package com.karlomaricevic.bluetoothmessagingapp.feature.addDevice

import com.karlomaricevic.bluetoothmessagingapp.dispatchers.IoDispatcherTag
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.viewmodel.AddDeviceViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

val addDeviceScreenModule = DI.Module("AddDeviceScreenModule") {

    bind<AddDeviceViewModel>() with provider { ->
        AddDeviceViewModel(
            observeDiscoverableState = instance(),
            getAvailableConnections = instance(),
            connectToServer = instance(),
            addContact = instance(),
            listenForConnection = instance(),
            navigator = instance(),
            ioDispatcher = instance(tag = IoDispatcherTag)
        )
    }
}
