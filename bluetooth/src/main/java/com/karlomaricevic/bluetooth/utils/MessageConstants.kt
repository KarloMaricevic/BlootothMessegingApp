package com.karlomaricevic.bluetooth.utils

object MessageConstants {
    const val DATA_SIZE_PREFIX_SIZE = Int.SIZE_BYTES
    const val MESSAGE_TYPE_PREFIX_SIZE = Int.SIZE_BYTES
    const val MESSAGE_TYPE_TEXT = 0
    const val MESSAGE_TYPE_IMAGE = 1
    const val MESSAGE_TYPE_AUDIO = 2
    const val CHUNK_SIZE = 1024
    val CHARSET_UTF_8 = Charsets.UTF_8
}
