package com.karlom.bluetoothmessagingapp.domain.bluetooth.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.karlom.bluetoothmessagingapp.data.bluetooth.AppBluetoothManager
import com.karlom.bluetoothmessagingapp.domain.bluetooth.models.BluetoothDevice
import com.karlom.bluetoothmessagingapp.domain.bluetooth.pager.AvailableBluetoothDevicePager
import javax.inject.Inject

class BluetoothRepository @Inject constructor(
    private val bluetoothManager: AppBluetoothManager,
) {

    private companion object {
        private const val PAGE_SIZE = 20
    }

    private suspend fun getAvailableBluetoothDevicesFromManager() =
        bluetoothManager.getAvailableBluetoothDevices().map { response ->
            response.map { deviceResponse -> BluetoothDevice(deviceResponse) }
        }

    fun getAvailableBluetoothDevices() = Pager(
        config = PagingConfig(PAGE_SIZE),
        pagingSourceFactory = { AvailableBluetoothDevicePager { getAvailableBluetoothDevicesFromManager() } },
    ).flow
}
