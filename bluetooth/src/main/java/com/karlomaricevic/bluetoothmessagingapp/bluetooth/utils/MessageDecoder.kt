package com.karlomaricevic.bluetoothmessagingapp.bluetooth.utils

import com.karlomaricevic.bluetoothmessagingapp.bluetooth.models.TransportMessage.*
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.utils.MessageConstants.CHARSET_UTF_8
import java.nio.ByteBuffer

class MessageDecoder {

    data class Header(val dataLength: Int, val messageType: Int)

    fun decodeHeader(headerBytes: ByteArray): Header {
        require(headerBytes.size >= MessageConstants.DATA_SIZE_PREFIX_SIZE + MessageConstants.MESSAGE_TYPE_PREFIX_SIZE) {
            "Header too short"
        }
        val dataLength = ByteBuffer.wrap(headerBytes, 0, MessageConstants.DATA_SIZE_PREFIX_SIZE).int
        val messageType = ByteBuffer.wrap(
            /* array = */ headerBytes,
            /* offset = */ MessageConstants.DATA_SIZE_PREFIX_SIZE,
            /* length = */ MessageConstants.MESSAGE_TYPE_PREFIX_SIZE
        ).int
        return Header(dataLength = dataLength, messageType = messageType)
    }

    fun decodeMessage(payload: ByteArray, messageType: Int, address: String) = when (messageType) {
        MessageConstants.MESSAGE_TYPE_TEXT -> Text(address, payload.toString(CHARSET_UTF_8))
        MessageConstants.MESSAGE_TYPE_IMAGE -> Image(address, payload)
        MessageConstants.MESSAGE_TYPE_AUDIO -> Audio(address, payload)
        else -> throw IllegalArgumentException("Unknown message type: $messageType")
    }
}
