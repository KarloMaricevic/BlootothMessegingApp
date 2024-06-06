package com.karlom.bluetoothmessagingapp.data.bluetooth.connectionManager

import com.karlom.bluetoothmessagingapp.data.bluetooth.models.SocketConnection
import kotlinx.coroutines.flow.Flow

interface ConnectionNotifier {

    val connectedDeviceNotifier: Flow<SocketConnection>
}
