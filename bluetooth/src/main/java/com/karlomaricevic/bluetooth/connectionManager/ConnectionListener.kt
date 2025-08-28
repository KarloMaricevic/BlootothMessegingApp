package com.karlomaricevic.bluetooth.connectionManager

import com.karlomaricevic.bluetooth.models.SocketStreams

interface ConnectionStateListener {
    fun onConnectionOpened(address: String, streams: SocketStreams)
    fun onConnectionClosed(address: String)
}