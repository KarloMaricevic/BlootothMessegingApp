package com.karlom.bluetoothmessagingapp.domain.connection.models

import com.karlom.bluetoothmessagingapp.data.bluetooth.models.BluetoothDeviceResponse

data class Connection(
    val name: String,
    val address: String,
) {
    constructor(response: BluetoothDeviceResponse) : this(
        name = response.name,
        address = response.address,
    )
}
