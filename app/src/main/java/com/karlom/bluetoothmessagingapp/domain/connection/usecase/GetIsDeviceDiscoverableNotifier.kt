package com.karlom.bluetoothmessagingapp.domain.connection.usecase

import com.karlom.bluetoothmessagingapp.data.bluetooth.AppBluetoothManager
import javax.inject.Inject

class GetIsDeviceDiscoverableNotifier @Inject constructor(
    private val appBluetoothManager: AppBluetoothManager,
) {
    operator fun invoke() = appBluetoothManager.getIsDeviceDiscoverableNotifier()
}
