package com.karlom.bluetoothmessagingapp.domain.bluetooth.usecase

import com.karlom.bluetoothmessagingapp.data.bluetooth.AppBluetoothManager
import javax.inject.Inject

class GetClientConnectedToMyServerNotifier @Inject constructor(
    private val bluetoothManager: AppBluetoothManager,
) {

    operator fun invoke() = bluetoothManager.getClientConnectedMyServerEventFlow()
}
