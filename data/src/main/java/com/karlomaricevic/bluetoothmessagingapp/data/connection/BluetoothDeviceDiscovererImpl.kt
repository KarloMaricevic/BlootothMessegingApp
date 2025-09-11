package com.karlomaricevic.bluetoothmessagingapp.data.connection

import com.karlomaricevic.bluetoothmessagingapp.bluetooth.AppBluetoothManager
import com.karlomaricevic.bluetoothmessagingapp.domain.connection.DeviceDiscoverer

class BluetoothDeviceDiscovererImpl(
    private val bluetoothManager: AppBluetoothManager
) : DeviceDiscoverer {

    override suspend fun discoverAvailableConnections() =
        bluetoothManager.getAvailableBluetoothDevices()

    override fun observeDiscoverableState() = bluetoothManager.getIsDeviceDiscoverableNotifier()
}
