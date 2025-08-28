package com.karlomaricevic.bluetooth.communicationMenager

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.left
import arrow.core.right
import com.karlomaricevic.bluetooth.connectionManager.BluetoothConnectionClient
import com.karlomaricevic.bluetooth.connectionManager.ConnectionStateListener
import com.karlomaricevic.bluetooth.models.SocketStreams
import com.karlomaricevic.bluetooth.models.TransportMessage
import com.karlomaricevic.bluetooth.utils.MessageConstants
import com.karlomaricevic.bluetooth.utils.MessageConstants.CHUNK_SIZE
import com.karlomaricevic.bluetooth.utils.MessageDecoder
import com.karlomaricevic.bluetooth.utils.MessageEncoder
import com.karlomaricevic.core.common.Failure.ErrorMessage
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BluetoothCommunicationManager(
    private val connectionManager: BluetoothConnectionClient,
    private val ioDispatcher: CoroutineDispatcher,
    private val encoder: MessageEncoder,
    private val decoder: MessageDecoder,
) : ConnectionStateListener {

    private val _receivedMessageEvent = Channel<TransportMessage>(Channel.BUFFERED)
    val receivedMessageEvent = _receivedMessageEvent.receiveAsFlow()

    private var readingJob: Job? = null

    init {
        connectionManager.registerConnectionStateListener(this)
    }

    override fun onConnectionOpened(address: String, streams: SocketStreams) {
        readingJob = startReadingInputStream(
            inputStream = streams.inputStream,
            address = address,
        )
    }

    override fun onConnectionClosed(address: String) {
        readingJob?.cancel()
    }

    suspend fun sendText(
        text: String,
    ): Either<ErrorMessage, Unit> = withOutputStream { output ->
        val encoded = encoder.encodeText(text)
        output.write(encoded)
    }

    suspend fun sendImage(
        stream: InputStream,
        size: Int,
    ): Either<ErrorMessage, Unit> = withOutputStream { output ->
        output.write(encoder.encodeImageHeader(size))
        stream.copyTo(out = output, bufferSize = CHUNK_SIZE)
    }

    suspend fun sendAudio(
        stream: InputStream,
        size: Int,
    ): Either<ErrorMessage, Unit> = withOutputStream { output ->
        output.write(encoder.encodeAudioHeader(size))
        stream.copyTo(out = output, bufferSize = CHUNK_SIZE)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun startReadingInputStream(
        inputStream: InputStream,
        address: String,
    ) = GlobalScope.launch(ioDispatcher) {
        val headerSize = MessageConstants.DATA_SIZE_PREFIX_SIZE + MessageConstants.MESSAGE_TYPE_PREFIX_SIZE
        val headerBuffer = ByteArray(headerSize)
        while (true) {
            try {
                inputStream.readFully(headerBuffer)
                val (dataLength, messageType) = decoder.decodeHeader(headerBuffer)

                val payload = ByteArray(dataLength)
                var bytesReadTotal = 0
                while (bytesReadTotal < dataLength) {
                    val chunkSize = minOf(MessageConstants.CHUNK_SIZE, dataLength - bytesReadTotal)
                    val read = inputStream.read(payload, bytesReadTotal, chunkSize)
                    if (read == -1) throw IOException("Stream closed unexpectedly")
                    bytesReadTotal += read
                }
                val message = decoder.decodeMessage(
                    payload = payload,
                    messageType = messageType,
                    address = address,
                )
                _receivedMessageEvent.send(message)

            } catch (_: IOException) {
                readingJob?.cancel()
                connectionManager.closeConnection()
                break
            }
        }
    }

    private suspend inline fun withOutputStream(
        crossinline block: (output: OutputStream) -> Unit
    ): Either<ErrorMessage, Unit> {
        return when (val outputStream = connectionManager.getOutputStream()) {
            is Left -> outputStream
            is Right -> {
                try {
                    withContext(ioDispatcher) {
                        block(outputStream.value)
                    }
                    Unit.right()
                } catch (e: Exception) {
                    connectionManager.closeConnection()
                    ErrorMessage(e.message ?: "Unknown").left()
                }
            }
        }
    }

    private fun InputStream.readFully(buffer: ByteArray) {
        var offset = 0
        while (offset < buffer.size) {
            val read = this.read(buffer, offset, buffer.size - offset)
            if (read == -1) throw IOException("Stream closed unexpectedly")
            offset += read
        }
    }
}
