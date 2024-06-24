package com.karlom.bluetoothmessagingapp.data.bluetooth

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.karlom.bluetoothmessagingapp.data.bluetooth.pager.AvailableConnectionsPager
import com.karlom.bluetoothmessagingapp.domain.connection.models.Connection
import javax.inject.Inject

class BluetoothRepository @Inject constructor(
    private val bluetoothManager: AppBluetoothManager,
) {

    private companion object {
        private const val PAGE_SIZE = 20
    }

    private suspend fun getAvailableBluetoothDevicesFromManager() =
        bluetoothManager.getAvailableBluetoothDevices().map { response ->
            response.map { deviceResponse -> Connection(deviceResponse) }
        }

    fun getAvailableBluetoothDevices() = Pager(
        config = PagingConfig(PAGE_SIZE),
        pagingSourceFactory = { AvailableConnectionsPager { getAvailableBluetoothDevicesFromManager() } },
    ).flow
}
