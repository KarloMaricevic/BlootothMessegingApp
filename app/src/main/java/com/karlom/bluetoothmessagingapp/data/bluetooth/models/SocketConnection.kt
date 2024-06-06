package com.karlom.bluetoothmessagingapp.data.bluetooth.models

import android.bluetooth.BluetoothSocket

data class SocketConnection(
    val bluetoothSocket: BluetoothSocket,
    val isConnected: Boolean,
)
