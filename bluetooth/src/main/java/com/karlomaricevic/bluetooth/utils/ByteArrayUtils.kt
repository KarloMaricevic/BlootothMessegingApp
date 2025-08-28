package com.karlomaricevic.bluetooth.utils;

fun bytesToInt(bytes: ByteArray): Int {
    bytes[0].toInt()
    require(bytes.size == 4) { "Byte array must be of length 4 to convert to Int" }
    return (bytes[0].toInt() and 0xFF shl 24) or
            (bytes[1].toInt() and 0xFF shl 16) or
            (bytes[2].toInt() and 0xFF shl 8) or
            (bytes[3].toInt() and 0xFF)
}
