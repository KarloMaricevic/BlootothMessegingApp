package com.karlom.bluetoothmessagingapp.data.bluetooth

import app.cash.turbine.test
import arrow.core.Either
import com.karlom.bluetoothmessagingapp.core.utils.bytesToInt
import com.karlom.bluetoothmessagingapp.data.bluetooth.communicationMenager.BluetoothCommunicationManager
import com.karlom.bluetoothmessagingapp.data.bluetooth.connectionManager.BluetoothConnectionManager
import com.karlom.bluetoothmessagingapp.data.bluetooth.models.BluetoothMessage
import com.karlom.bluetoothmessagingapp.data.bluetooth.models.SocketStreams
import com.karlom.bluetoothmessagingapp.ruless.MainDispatcherRule
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer

class BluetoothCommunicationManagerTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val connectionManager = mockk<BluetoothConnectionManager>()

    private val outputStream = ByteArrayOutputStream()
    private val deviceAddress = "address"
    private val textMessage = "Test message!"
    private val textMessageType = 0
    private val imageMessageType = 1
    private val imageByteArray = ByteArray(2350) { index -> index.toByte() }
    private val imageStream = ByteArrayInputStream(imageByteArray)
    private val textInputStream = ByteArrayInputStream(
        ByteBuffer.allocate(Int.SIZE_BYTES).apply { putInt(textMessage.length) }.array() +
            ByteBuffer.allocate(Int.SIZE_BYTES).apply { putInt(textMessageType) }.array() +
            textMessage.toByteArray()
    )
    private val imageInputStream = ByteArrayInputStream(
        ByteBuffer.allocate(Int.SIZE_BYTES).apply { putInt(imageByteArray.size) }.array() +
            ByteBuffer.allocate(Int.SIZE_BYTES).apply { putInt(imageMessageType) }.array() +
            imageByteArray
    )
    private val exceptionInputStream = object : InputStream() {
        override fun read(): Int {
            throw IOException("Forced exception")
        }
    }

    @Before
    fun setUp() {
        every { connectionManager.getOutputStream() } returns Either.Right(outputStream)
        every { connectionManager.registerConnectionStateListener(any()) } returns Unit
        every { connectionManager.closeConnection() } returns Unit
    }

    @After
    fun tearDown() {
        outputStream.close()
        textInputStream.close()
        imageInputStream.close()
    }

    @Test
    fun shouldSendRightDataLengthWhenSendingString() = runTest {
        val sut = BluetoothCommunicationManager(
            connectionManager = connectionManager,
            ioDispatcher = mainDispatcherRule.testDispatcher,
        )

        sut.sendText(text = textMessage)

        val dataLengthIndicator = outputStream.toByteArray().sliceArray(0 until 4)
        assertEquals(textMessage.length, bytesToInt(dataLengthIndicator))
    }

    @Test
    fun shouldSendRightTypeIndicatorWhenSendingString() = runTest {
        val sut = BluetoothCommunicationManager(
            connectionManager = connectionManager,
            ioDispatcher = mainDispatcherRule.testDispatcher,
        )

        sut.sendText(text = textMessage)

        val typeIndicator = outputStream.toByteArray().sliceArray(4 until 8)
        assertEquals(textMessageType, bytesToInt(typeIndicator))
    }

    @Test
    fun shouldSendUTF8CodedStringAsDataWhenSendingString() = runTest {
        val sut = BluetoothCommunicationManager(
            connectionManager = connectionManager,
            ioDispatcher = mainDispatcherRule.testDispatcher,
        )

        sut.sendText(text = textMessage)

        val data = outputStream.toByteArray().sliceArray(8 until 8 + textMessage.length)
        data shouldBe textMessage.toByteArray(Charsets.UTF_8)
    }

    @Test
    fun shouldSendRightDataLengthWhenSendingImage() = runTest {
        val sut = BluetoothCommunicationManager(
            connectionManager = connectionManager,
            ioDispatcher = mainDispatcherRule.testDispatcher,
        )

        sut.sendImage(
            stream = imageStream,
            streamSize = imageByteArray.size,
        )

        val dataLengthIndicator = outputStream.toByteArray().sliceArray(0 until 4)
        assertEquals(imageByteArray.size, bytesToInt(dataLengthIndicator))
    }

    @Test
    fun shouldSendRightTypeIndicatorWhenSendingImage() = runTest {
        val sut = BluetoothCommunicationManager(
            connectionManager = connectionManager,
            ioDispatcher = mainDispatcherRule.testDispatcher,
        )

        sut.sendImage(
            stream = imageStream,
            streamSize = imageByteArray.size,
        )

        val typeIndicator = outputStream.toByteArray().sliceArray(4 until 8)
        assertEquals(imageMessageType, bytesToInt(typeIndicator))
    }

    @Test
    fun shouldSendCorrectImageDataWhenSendingImage() = runTest {
        val sut = BluetoothCommunicationManager(
            connectionManager = connectionManager,
            ioDispatcher = mainDispatcherRule.testDispatcher,
        )

        sut.sendImage(
            stream = imageStream,
            streamSize = imageByteArray.size,
        )

        val sentMessage = outputStream.toByteArray()
        val data = outputStream.toByteArray().sliceArray(8 until sentMessage.size)
        data shouldBe imageByteArray
    }

    @Test
    fun shouldEmitTextMessageWhenTextMessageIsReceived() = runTest {
        val sut = BluetoothCommunicationManager(
            connectionManager = connectionManager,
            ioDispatcher = mainDispatcherRule.testDispatcher,
        )

        sut.receivedMessageEvent.test {
            sut.onConnectionOpened(
                address = deviceAddress,
                streams = SocketStreams(
                    outputStream = ByteArrayOutputStream(),
                    inputStream = textInputStream,
                )
            )

            awaitItem() shouldBe BluetoothMessage.Text(deviceAddress, textMessage)
        }
    }

    @Test
    fun shouldEmitImageMessageWhenImageMessageIsReceived() = runTest {
        val sut = BluetoothCommunicationManager(
            connectionManager = connectionManager,
            ioDispatcher = mainDispatcherRule.testDispatcher,
        )

        sut.receivedMessageEvent.test {
            sut.onConnectionOpened(
                address = deviceAddress,
                streams = SocketStreams(
                    outputStream = ByteArrayOutputStream(),
                    inputStream = imageInputStream,
                )
            )

            awaitItem() shouldBe BluetoothMessage.Image(deviceAddress, imageByteArray)
        }
    }



    @Test
    fun shouldTerminateConnectionIfIOExceptionAccusesWhileReading() = runTest {
        val sut = BluetoothCommunicationManager(
            connectionManager = connectionManager,
            ioDispatcher = mainDispatcherRule.testDispatcher,
        )

        sut.onConnectionOpened(
            address = deviceAddress,
            streams = SocketStreams(
                outputStream = ByteArrayOutputStream(),
                inputStream = exceptionInputStream,
            )
        )

        verify { connectionManager.closeConnection() }
    }
}
