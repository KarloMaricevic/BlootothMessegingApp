package com.karlom.bluetoothmessagingapp.feature.bluetoothDevices.viewmodel

import com.karlom.bluetoothmessagingapp.core.base.BaseViewModel
import com.karlom.bluetoothmessagingapp.domain.bluetooth.models.BluetoothDevice
import com.karlom.bluetoothmessagingapp.feature.bluetoothDevices.models.BluetoothDevicesScreenEvent
import com.karlom.bluetoothmessagingapp.feature.bluetoothDevices.models.BluetoothDevicesScreenEvent.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class BluetoothDevicesViewModel : BaseViewModel<BluetoothDevicesScreenEvent>() {

    val devices = MutableStateFlow<List<BluetoothDevice>?>(null)
    override fun onEvent(event: BluetoothDevicesScreenEvent) {
        when (event) {
            is OnScanForDevicesClicked -> devices.update {
                listOf(
                    BluetoothDevice("Device1", "FA:92:46:11:02:43"),
                    BluetoothDevice("Device2", "1E:92:32:11:42:14"),
                    BluetoothDevice("Device3", "BC:43:64:11:29:16"),
                )
            }
        }
    }
}
