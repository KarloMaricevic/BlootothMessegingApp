package com.karlomaricevic.bluetoothmessagingapp.bluetooth.models

import java.io.InputStream
import java.io.OutputStream

data class SocketStreams(
    val outputStream: OutputStream,
    val inputStream: InputStream,
)
