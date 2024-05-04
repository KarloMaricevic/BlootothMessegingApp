package com.karlom.bluetoothmessagingapp.feature.bluetoothDevices.router

import com.karlom.bluetoothmessagingapp.core.navigation.NavigationDestination

object BluetoothDevicesRouter : NavigationDestination {

    private const val BLUETOOTH_DEVICES_SCREEN_ROUTE = "bluetoothDevices"

    override fun route() = BLUETOOTH_DEVICES_SCREEN_ROUTE
}
