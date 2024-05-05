package com.karlom.bluetoothmessagingapp.domain.bluetooth.usecase

import com.karlom.bluetoothmessagingapp.domain.bluetooth.repository.BluetoothRepository
import javax.inject.Inject

class GetAvailableBluetoothDevices @Inject constructor(
    private val repository: BluetoothRepository,
) {

    operator fun invoke() = repository.getAvailableBluetoothDevices()
}
