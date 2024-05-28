package com.karlom.bluetoothmessagingapp.data.bluetooth.models

import com.karlom.bluetoothmessagingapp.domain.bluetooth.models.BluetoothDevice

sealed interface ConnectionState {
    data object NotConnected : ConnectionState
    data class Connected(val device: BluetoothDevice) : ConnectionState
}
