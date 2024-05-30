package com.karlom.bluetoothmessagingapp.data.bluetooth.connectionManager

import android.bluetooth.BluetoothSocket
import kotlinx.coroutines.flow.Flow

interface ConnectionNotifier {

    fun getNotifier(): Flow<BluetoothSocket?>
}
