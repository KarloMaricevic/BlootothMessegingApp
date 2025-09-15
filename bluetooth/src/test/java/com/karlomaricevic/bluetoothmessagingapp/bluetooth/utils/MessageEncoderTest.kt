package com.karlomaricevic.bluetoothmessagingapp.bluetooth.utils

import com.karlomaricevic.bluetoothmessagingapp.bluetooth.utils.MessageConstants.DATA_SIZE_PREFIX_SIZE
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.utils.MessageConstants.MESSAGE_TYPE_AUDIO
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.utils.MessageConstants.MESSAGE_TYPE_IMAGE
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.utils.MessageConstants.MESSAGE_TYPE_PREFIX_SIZE
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.utils.MessageConstants.MESSAGE_TYPE_TEXT
import io.kotest.matchers.shouldBe
import java.nio.ByteBuffer
import org.junit.Test

class MessageEncoderTest {

    private val encoder = MessageEncoder()

    @Test
    fun encodeText_ValidText_ReturnsCorrectByteArray() {
        val text = "Hello"
        val result = encoder.encodeText(text)

        val expectedData = text.toByteArray(Charsets.UTF_8)
        val expectedSize = ByteBuffer.allocate(DATA_SIZE_PREFIX_SIZE).putInt(expectedData.size).array()
        val expectedType = ByteBuffer.allocate(MESSAGE_TYPE_PREFIX_SIZE).putInt(MESSAGE_TYPE_TEXT).array()
        val expected = expectedSize + expectedType + expectedData

        result shouldBe expected
    }

    @Test
    fun encodeBinaryHeader_ValidTypeAndSize_ReturnsCorrectByteArray() {
        val type = 42
        val streamSize = 123
        val result = encoder.encodeBinaryHeader(type, streamSize)

        val expectedSize = ByteBuffer.allocate(DATA_SIZE_PREFIX_SIZE).putInt(streamSize).array()
        val expectedType = ByteBuffer.allocate(MESSAGE_TYPE_PREFIX_SIZE).putInt(type).array()
        val expected = expectedSize + expectedType

        result shouldBe expected
    }

    @Test
    fun encodeImageHeader_ValidSize_ReturnsCorrectByteArray() {
        val streamSize = 999
        val result = encoder.encodeImageHeader(streamSize)

        val expectedSize = ByteBuffer.allocate(DATA_SIZE_PREFIX_SIZE).putInt(streamSize).array()
        val expectedType = ByteBuffer.allocate(MESSAGE_TYPE_PREFIX_SIZE).putInt(MESSAGE_TYPE_IMAGE).array()
        val expected = expectedSize + expectedType

        result shouldBe expected
    }

    @Test
    fun encodeAudioHeader_ValidSize_ReturnsCorrectByteArray() {
        val streamSize = 888
        val result = encoder.encodeAudioHeader(streamSize)

        val expectedSize = ByteBuffer.allocate(DATA_SIZE_PREFIX_SIZE).putInt(streamSize).array()
        val expectedType = ByteBuffer.allocate(MESSAGE_TYPE_PREFIX_SIZE).putInt(MESSAGE_TYPE_AUDIO).array()
        val expected = expectedSize + expectedType

        result shouldBe expected
    }
}
