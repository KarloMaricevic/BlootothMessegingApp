package com.karlomaricevic.bluetoothmessagingapp.bluetooth.connectionManager

import com.karlomaricevic.bluetoothmessagingapp.bluetooth.models.SocketStreams

interface ConnectionStateListener {
    fun onConnectionOpened(address: String, streams: SocketStreams)
    fun onConnectionClosed(address: String)
}