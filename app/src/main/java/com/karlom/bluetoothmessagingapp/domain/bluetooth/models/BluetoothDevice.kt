package com.karlom.bluetoothmessagingapp.domain.bluetooth.models

import com.karlom.bluetoothmessagingapp.data.bluetooth.models.BluetoothDeviceResponse

data class BluetoothDevice(
    val name: String,
    val address: String,
) {
    constructor(response: BluetoothDeviceResponse) : this(
        name = response.name,
        address = response.address,
    )
}
