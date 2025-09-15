package com.karlomaricevic.bluetoothmessagingapp.bluetooth.utils

import com.karlomaricevic.bluetoothmessagingapp.bluetooth.utils.MessageConstants.DATA_SIZE_PREFIX_SIZE
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.utils.MessageConstants.MESSAGE_TYPE_AUDIO
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.utils.MessageConstants.MESSAGE_TYPE_IMAGE
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.utils.MessageConstants.MESSAGE_TYPE_PREFIX_SIZE
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.utils.MessageConstants.MESSAGE_TYPE_TEXT
import java.nio.ByteBuffer

class MessageEncoder {
    fun encodeText(text: String): ByteArray {
        val dataArray = text.toByteArray(Charsets.UTF_8)
        val sizeArray = ByteBuffer.allocate(DATA_SIZE_PREFIX_SIZE).putInt(dataArray.size).array()
        val typeArray = ByteBuffer.allocate(MESSAGE_TYPE_PREFIX_SIZE).putInt(MESSAGE_TYPE_TEXT).array()
        return sizeArray + typeArray + dataArray
    }

    fun encodeBinaryHeader(type: Int, streamSize: Int): ByteArray {
        val sizeArray = ByteBuffer.allocate(DATA_SIZE_PREFIX_SIZE).putInt(streamSize).array()
        val typeArray = ByteBuffer.allocate(MESSAGE_TYPE_PREFIX_SIZE).putInt(type).array()
        return sizeArray + typeArray
    }

    fun encodeImageHeader(streamSize: Int): ByteArray =
        encodeBinaryHeader(MESSAGE_TYPE_IMAGE, streamSize)

    fun encodeAudioHeader(streamSize: Int): ByteArray =
        encodeBinaryHeader(MESSAGE_TYPE_AUDIO, streamSize)
}
