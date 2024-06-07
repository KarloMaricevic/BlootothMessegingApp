package com.karlom.bluetoothmessagingapp.data.bluetooth.connectionManager

import com.karlom.bluetoothmessagingapp.data.bluetooth.models.SocketStreams

interface ConnectionStateListener {
    fun onConnectionOpened(address: String, streams: SocketStreams)
    fun onConnectionClosed(address: String)
}