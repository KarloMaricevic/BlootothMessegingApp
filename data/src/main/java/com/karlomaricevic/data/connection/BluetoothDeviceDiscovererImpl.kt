package com.karlomaricevic.data.connection

import com.karlomaricevic.bluetooth.AppBluetoothManager
import com.karlomaricevic.domain.connection.DeviceDiscoverer

class BluetoothDeviceDiscovererImpl(
    private val bluetoothManager: AppBluetoothManager
) : DeviceDiscoverer {

    override suspend fun discoverAvailableConnections() =
        bluetoothManager.getAvailableBluetoothDevices()

    override fun observeDiscoverableState() = bluetoothManager.getIsDeviceDiscoverableNotifier()
}
