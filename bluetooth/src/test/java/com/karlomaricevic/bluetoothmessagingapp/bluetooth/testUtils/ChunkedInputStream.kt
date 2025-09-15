package com.karlomaricevic.bluetoothmessagingapp.bluetooth.testUtils

import java.io.InputStream

class ChunkedInputStream(fullMessage: ByteArray): InputStream() {
    val data = fullMessage
    var index = 0

    override fun read(): Int {
        return if (index < data.size) {
            data[index++].toInt() and 0xFF
        } else {
            -1 // End of stream
        }
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        if (index >= data.size) return -1

        val bytesToRead = minOf(len, 2, data.size - index) // Simulate very small reads
        for (i in 0 until bytesToRead) {
            b[off + i] = data[index++]
        }
        return bytesToRead
    }
}