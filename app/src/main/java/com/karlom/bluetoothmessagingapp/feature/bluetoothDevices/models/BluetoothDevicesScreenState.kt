package com.karlom.bluetoothmessagingapp.feature.bluetoothDevices.models

import androidx.paging.PagingData
import com.karlom.bluetoothmessagingapp.domain.bluetooth.models.BluetoothDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

data class BluetoothDevicesScreenState(
    val showSearchButton: Boolean = true,
    val devices: Flow<PagingData<BluetoothDevice>> = flowOf(),
)
