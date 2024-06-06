package com.karlom.bluetoothmessagingapp.data.bluetooth.models

sealed class BluetoothMessage(val address: String) {

    class Text(
        address: String,
        val text: String
    ) : BluetoothMessage(address)

    class Image(
        address: String,
        val image: ByteArray
    ) : BluetoothMessage(address)

    class Audio(
        address: String,
        val audio: ByteArray
    ) : BluetoothMessage(address)
}
