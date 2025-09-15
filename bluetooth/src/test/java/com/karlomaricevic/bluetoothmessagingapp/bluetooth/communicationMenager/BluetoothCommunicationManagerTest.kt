package com.karlomaricevic.bluetoothmessagingapp.bluetooth.communicationMenager

import arrow.core.right
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.connectionManager.BluetoothConnectionClient
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.models.SocketStreams
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.models.TransportMessage
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.testUtils.ChunkedInputStream
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.utils.MessageDecoder
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.utils.MessageDecoder.Header
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.utils.MessageEncoder
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class BluetoothCommunicationManagerTest {

    private val testDispatcher = StandardTestDispatcher()
    val listeningScope = CoroutineScope(Job() + testDispatcher)

    private val connectionManager = mockk<BluetoothConnectionClient>()
    private val encoder = mockk<MessageEncoder>()
    private val decoder = mockk<MessageDecoder>()
    private lateinit var sut: BluetoothCommunicationManager

    private val outputStream = ByteArrayOutputStream()

    @Before
    fun setUp() {
        every { connectionManager.registerConnectionStateListener(any()) } just Runs
        every { connectionManager.getOutputStream() } returns outputStream.right()
        sut = BluetoothCommunicationManager(
            connectionManager = connectionManager,
            ioDispatcher = testDispatcher,
            encoder = encoder,
            decoder = decoder,
            listeningScope = listeningScope,
        )
    }

    @After
    fun cleanUp() {
        listeningScope.cancel()
    }

    @Test
    fun sendText_ValidText_WritesEncodedBytesToOutputStream() = runTest(testDispatcher) {
        every { connectionManager.registerConnectionStateListener(any()) } just Runs
        every { connectionManager.closeConnection() } just Runs
        val text = "Hello World"
        val encodedBytes = text.toByteArray()
        every { encoder.encodeText(text) } returns encodedBytes

        val result = sut.sendText(text)

        result.isRight() shouldBe true
        outputStream.toByteArray() shouldBe encodedBytes
    }

    @Test
    fun sendImage_ValidStream_WritesHeaderAndDataToOutputStream() = runTest(testDispatcher) {
        val imageData = byteArrayOf(1, 2, 3, 4, 5)
        val imageStream = ByteArrayInputStream(imageData)
        val imageSize = imageData.size
        val headerBytes = byteArrayOf(9, 9, 9)
        every { encoder.encodeImageHeader(imageSize) } returns headerBytes

        val result = sut.sendImage(imageStream, imageSize)

        result.isRight() shouldBe true
        val writtenBytes = outputStream.toByteArray()
        val expectedBytes = headerBytes + imageData
        writtenBytes shouldBe expectedBytes
    }

    @Test
    fun sendAudio_ValidStream_WritesHeaderAndDataToOutputStream() = runTest(testDispatcher) {
        val audioData = byteArrayOf(10, 20, 30, 40)
        val audioStream = ByteArrayInputStream(audioData)
        val audioSize = audioData.size
        val headerBytes = byteArrayOf(7, 7, 7)
        every { encoder.encodeAudioHeader(audioSize) } returns headerBytes

        val result = sut.sendAudio(audioStream, audioSize)

        result.isRight() shouldBe true
        val writtenBytes = outputStream.toByteArray()
        val expectedBytes = headerBytes + audioData
        writtenBytes shouldBe expectedBytes
    }


    @Test
    fun startReadingInputStream_ValidMessageGottenFromSingleTransmission_EmitsDecodedMessage() = runTest(testDispatcher) {
        every { connectionManager.closeConnection() } just Runs
        val textToSend = "hello"
        val payload = textToSend.toByteArray()
        val messageType = 1
        val size = payload.size
        val type = messageType
        val header = buildHeader(size, type)
        val inputStream = ByteArrayInputStream(header + payload + header)
        val address = "deviceAddress"
        every { decoder.decodeHeader(header) } returns Header(
            dataLength = payload.size,
            messageType = messageType,
        )
        every { decoder.decodeMessage(payload, messageType, address) } returns
            TransportMessage.Text(
                address = address,
                text = textToSend,
            )

        sut.onConnectionOpened(address, SocketStreams(
            inputStream = inputStream,
            outputStream = ByteArrayOutputStream.nullOutputStream(),
        ))
        testDispatcher.scheduler.advanceUntilIdle()

        val emitted = sut.receivedMessageEvent.first()
        emitted shouldBe TransportMessage.Text(address, textToSend)
        sut.onConnectionClosed(address)
    }

    @Test
    fun startReadingInputStream_MessageArrivesInChunks_EmitsDecodedMessage() = runTest(testDispatcher) {
        every { connectionManager.closeConnection() } just Runs
        val textToSend = "hello"
        val payload = textToSend.toByteArray()
        val messageType = 1
        val address = "deviceAddress"
        val size = payload.size
        val type = messageType
        val header = buildHeader(size, type)
        val fullMessage = header + payload
        val chunkedInputStream = ChunkedInputStream(fullMessage)
        every { decoder.decodeHeader(header) } returns Header(
            dataLength = payload.size,
            messageType = messageType,
        )
        every { decoder.decodeMessage(payload, messageType, address) } returns
            TransportMessage.Text(
                address = address,
                text = textToSend,
            )

        sut.onConnectionOpened(address, SocketStreams(
            inputStream = chunkedInputStream,
            outputStream = ByteArrayOutputStream.nullOutputStream(),
        ))
        testDispatcher.scheduler.advanceUntilIdle()

        val emitted = sut.receivedMessageEvent.first()
        emitted shouldBe TransportMessage.Text(address, textToSend)
        sut.onConnectionClosed(address)
    }

    @Test
    fun startReadingInputStream_WhenIOExceptionOccurs_ClosesConnectionAndCancelsJob() = runTest(testDispatcher) {
        val address = "deviceAddress"
        val inputStream = object : InputStream() {
            override fun read(b: ByteArray, off: Int, len: Int): Int {
                throw IOException("Simulated device disconnect")
            }

            override fun read(): Int {
                throw IOException("Simulated device disconnect")
            }
        }
        every { connectionManager.closeConnection() } just Runs

        sut.onConnectionOpened(
            address,
            SocketStreams(
                inputStream = inputStream,
                outputStream = ByteArrayOutputStream.nullOutputStream()
            )
        )
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(listeningScope.coroutineContext[Job]!!.children.any().not())
        verify { connectionManager.closeConnection() }
    }

    private fun buildHeader(size: Int, type: Int): ByteArray {
        return byteArrayOf(
            (size shr 24).toByte(),
            (size shr 16).toByte(),
            (size shr 8).toByte(),
            size.toByte(),
            (type shr 24).toByte(),
            (type shr 16).toByte(),
            (type shr 8).toByte(),
            type.toByte()
        )
    }
}
