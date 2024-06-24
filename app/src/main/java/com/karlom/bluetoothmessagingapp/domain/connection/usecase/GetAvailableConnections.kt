package com.karlom.bluetoothmessagingapp.domain.connection.usecase

import com.karlom.bluetoothmessagingapp.data.bluetooth.BluetoothRepository
import javax.inject.Inject

class GetAvailableConnections @Inject constructor(
    private val repository: BluetoothRepository,
) {

    operator fun invoke() = repository.getAvailableBluetoothDevices()
}
