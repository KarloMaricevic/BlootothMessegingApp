package com.karlomaricevic.bluetoothmessagingapp.bluetooth.utils

import com.karlomaricevic.bluetoothmessagingapp.bluetooth.models.TransportMessage.Audio
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.models.TransportMessage.Image
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.models.TransportMessage.Text
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.utils.MessageConstants.CHARSET_UTF_8
import io.kotest.assertions.throwables.shouldThrow
import org.junit.Test
import java.nio.ByteBuffer
import io.kotest.matchers.shouldBe

class MessageDecoderTest {

    private val decoder = MessageDecoder()
    @Test
    fun decodeHeader_ValidHeader_ReturnsCorrectHeader() {
        val dataLength = 1234
        val messageType = MessageConstants.MESSAGE_TYPE_IMAGE
        val headerBytes = ByteBuffer.allocate(MessageConstants.DATA_SIZE_PREFIX_SIZE + MessageConstants.MESSAGE_TYPE_PREFIX_SIZE)
            .putInt(dataLength)
            .putInt(messageType)
            .array()

        val header = decoder.decodeHeader(headerBytes)

        header.dataLength shouldBe dataLength
        header.messageType shouldBe messageType
    }

    @Test
    fun decodeHeader_HeaderTooShort_ThrowsException() {
        val shortHeader = ByteArray(2)

        val exception = shouldThrow<IllegalArgumentException> {
            decoder.decodeHeader(shortHeader)
        }
        exception.message shouldBe "Header too short"
    }

    @Test
    fun decodeMessage_TextType_ReturnsTextObject() {
        val address = "device1"
        val payloadText = "Hello World"
        val payloadBytes = payloadText.toByteArray(CHARSET_UTF_8)

        val message = decoder.decodeMessage(payloadBytes, MessageConstants.MESSAGE_TYPE_TEXT, address)

        message shouldBe Text(address, payloadText)
    }

    @Test
    fun decodeMessage_ImageType_ReturnsImageObject() {
        val address = "device2"
        val payloadBytes = ByteArray(10) { it.toByte() }

        val message = decoder.decodeMessage(payloadBytes, MessageConstants.MESSAGE_TYPE_IMAGE, address)

        message shouldBe Image(address, payloadBytes)
    }

    @Test
    fun decodeMessage_AudioType_ReturnsAudioObject() {
        val address = "device3"
        val payloadBytes = ByteArray(5) { (it + 10).toByte() }

        val message = decoder.decodeMessage(payloadBytes, MessageConstants.MESSAGE_TYPE_AUDIO, address)

        message shouldBe Audio(address, payloadBytes)
    }

    @Test
    fun decodeMessage_UnknownType_ThrowsException() {
        val address = "deviceX"
        val payloadBytes = ByteArray(1)
        val unknownType = 99

        val exception = shouldThrow<IllegalArgumentException> {
            decoder.decodeMessage(payloadBytes, unknownType, address)
        }
        exception.message shouldBe "Unknown message type: $unknownType"
    }
}
