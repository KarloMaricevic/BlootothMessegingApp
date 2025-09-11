package com.karlomaricevic.bluetoothmessagingapp.data.connection

import com.karlomaricevic.data.connection.ChatConnectionManager
import com.karlomaricevic.bluetoothmessagingapp.domain.connection.ConnectionManager
import com.karlomaricevic.bluetoothmessagingapp.domain.connection.DeviceDiscoverer
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton


val connectionDataModule = DI.Module("ConnectionDataModule") {
    bind<DeviceDiscoverer>() with singleton { BluetoothDeviceDiscovererImpl(instance()) }
    bind<ConnectionManager>() with singleton { ChatConnectionManager(instance()) }
}
