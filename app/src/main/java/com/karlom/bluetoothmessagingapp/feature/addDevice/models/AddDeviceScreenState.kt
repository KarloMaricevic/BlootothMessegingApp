package com.karlom.bluetoothmessagingapp.feature.addDevice.models

import androidx.paging.PagingData
import com.karlom.bluetoothmessagingapp.domain.bluetooth.models.BluetoothDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

data class AddDeviceScreenState(
    val isDiscoverable: Boolean = true,
    val isBluetoothDeviceListShown: Boolean = false,
    val bluetoothDevices: Flow<PagingData<BluetoothDevice>> = flowOf(),
    val showMakeDeviceVisibleError: Boolean = false,
    val showConnectingToDeviceError: Boolean = false,
)
