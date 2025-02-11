package com.karlom.bluetoothmessagingapp.data.bluetooth.communicationMenager

import android.media.UnsupportedSchemeException
import arrow.core.Either
import com.karlom.bluetoothmessagingapp.core.di.IoDispatcher
import com.karlom.bluetoothmessagingapp.core.models.Failure
import com.karlom.bluetoothmessagingapp.core.utils.bytesToInt
import com.karlom.bluetoothmessagingapp.data.bluetooth.connectionManager.BluetoothConnectionManager
import com.karlom.bluetoothmessagingapp.data.bluetooth.connectionManager.ConnectionStateListener
import com.karlom.bluetoothmessagingapp.data.bluetooth.models.BluetoothMessage
import com.karlom.bluetoothmessagingapp.data.bluetooth.models.SocketStreams
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BluetoothCommunicationManager @Inject constructor(
    private val connectionManager: BluetoothConnectionManager,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ConnectionStateListener {

    private companion object {
        const val DATA_SIZE_PREFIX_SIZE = Int.SIZE_BYTES
        const val CHUNK_SIZE = 1024
        const val MESSAGE_TYPE_PREFIX_SIZE = Int.SIZE_BYTES
        const val MESSAGE_TYPE_TEXT_INDICATOR = 0
        const val MESSAGE_TYPE_IMAGE_INDICATOR = 1
        const val MESSAGE_TYPE_AUDIO_INDICATOR = 2
        val CHARSET_UTF_8 = Charsets.UTF_8
    }

    private val _receivedMessageEvent = Channel<BluetoothMessage>(Channel.BUFFERED)
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
    ): Either<Failure.ErrorMessage, Unit> {
        return when (val outputStream = connectionManager.getOutputStream()) {
            is Either.Left -> outputStream
            is Either.Right -> {
                withContext(ioDispatcher) {
                    try {
                        val dataArray = text.toByteArray(CHARSET_UTF_8)
                        val sizeBuffer = ByteBuffer.allocate(Int.SIZE_BYTES).putInt(dataArray.size)
                        val typeArray =
                            ByteBuffer.allocate(Int.SIZE_BYTES).putInt(MESSAGE_TYPE_TEXT_INDICATOR)
                        outputStream.value.write(sizeBuffer.array())
                        outputStream.value.write(typeArray.array())
                        outputStream.value.write(dataArray)
                        Either.Right(Unit)
                    } catch (error: IOException) {
                        connectionManager.closeConnection()
                        Either.Left(Failure.ErrorMessage(error.message ?: "Unknown"))
                    }
                }
            }
        }
    }

    suspend fun sendImage(
        stream: InputStream,
        streamSize: Int,
    ): Either<Failure.ErrorMessage, Unit> {
        return when (val outputStream = connectionManager.getOutputStream()) {
            is Either.Left -> outputStream
            is Either.Right -> {
                withContext(ioDispatcher) {
                    try {
                        val sizeArray =
                            ByteBuffer.allocate(DATA_SIZE_PREFIX_SIZE).putInt(streamSize).array()
                        val typeArray =
                            ByteBuffer.allocate(MESSAGE_TYPE_PREFIX_SIZE)
                                .putInt(MESSAGE_TYPE_IMAGE_INDICATOR)
                                .array()
                        val dataBuffer = ByteArray(CHUNK_SIZE)
                        var bytesRead: Int
                        outputStream.value.write(sizeArray)
                        outputStream.value.write(typeArray)
                        while (stream.read(dataBuffer).also { bytesRead = it } != -1) {
                            outputStream.value.write(dataBuffer, 0, bytesRead)
                        }
                        Either.Right(Unit)
                    } catch (e: Exception) {
                        connectionManager.closeConnection()
                        Either.Left(Failure.ErrorMessage(e.message ?: "Unknown"))
                    }
                }
            }
        }
    }

    suspend fun sendAudio(
        stream: InputStream,
        streamSize: Int,
    ): Either<Failure.ErrorMessage, Unit> {
        return when (val outputStream = connectionManager.getOutputStream()) {
            is Either.Left -> outputStream
            is Either.Right -> {
                withContext(ioDispatcher) {
                    try {
                        val sizeArray =
                            ByteBuffer.allocate(DATA_SIZE_PREFIX_SIZE).putInt(streamSize).array()
                        val typeArray =
                            ByteBuffer.allocate(MESSAGE_TYPE_PREFIX_SIZE)
                                .putInt(MESSAGE_TYPE_AUDIO_INDICATOR)
                                .array()
                        val dataBuffer = ByteArray(CHUNK_SIZE)
                        var bytesRead: Int
                        outputStream.value.write(sizeArray)
                        outputStream.value.write(typeArray)
                        while (stream.read(dataBuffer).also { bytesRead = it } != -1) {
                            outputStream.value.write(dataBuffer, 0, bytesRead)
                        }
                        Either.Right(Unit)
                    } catch (e: Exception) {
                        connectionManager.closeConnection()
                        Either.Left(Failure.ErrorMessage(e.message ?: "Unknown"))
                    }
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun startReadingInputStream(
        inputStream: InputStream,
        address: String,
    ) = GlobalScope.launch(ioDispatcher) {
        val inputBuffer = ByteArray(CHUNK_SIZE)
        var dataBuffer: ByteArray? = null
        var dataBitsWritten = 0
        var dataLength: Int? = null
        var dataType: Int? = null
        var isFirstChunkOfMessage = true
        while (true) {
            try {
                val bytesRead = inputStream.read(inputBuffer)
                if (bytesRead == -1) {
                    throw IOException()
                }
                if (isFirstChunkOfMessage) {
                    dataLength = bytesToInt(inputBuffer.copyOfRange(0, DATA_SIZE_PREFIX_SIZE))
                    dataType = bytesToInt(
                        inputBuffer.copyOfRange(
                            DATA_SIZE_PREFIX_SIZE,
                            DATA_SIZE_PREFIX_SIZE + MESSAGE_TYPE_PREFIX_SIZE
                        )
                    )
                    dataBuffer = ByteArray(dataLength.toInt())
                }
                System.arraycopy(
                    /* src = */ inputBuffer,
                    /* srcPos = */
                    if (isFirstChunkOfMessage) DATA_SIZE_PREFIX_SIZE + MESSAGE_TYPE_PREFIX_SIZE else 0,
                    /* dest = */
                    dataBuffer!!,
                    /* destPos = */
                    dataBitsWritten,
                    /* length = */
                    if (isFirstChunkOfMessage) {
                        if (inputBuffer.size - DATA_SIZE_PREFIX_SIZE - MESSAGE_TYPE_PREFIX_SIZE > dataLength!!) {
                            dataLength
                        } else {
                            inputBuffer.size - DATA_SIZE_PREFIX_SIZE - MESSAGE_TYPE_PREFIX_SIZE
                        }
                    } else {
                        if (dataBitsWritten + inputBuffer.size < dataLength!!) {
                            inputBuffer.size
                        } else {
                            dataLength - dataBitsWritten
                        }
                    }
                )
                dataBitsWritten += bytesRead - if (isFirstChunkOfMessage) (DATA_SIZE_PREFIX_SIZE + MESSAGE_TYPE_PREFIX_SIZE) else 0
                if (isFirstChunkOfMessage) {
                    isFirstChunkOfMessage = false
                }
                if (dataLength == dataBitsWritten) {
                    val message = when (dataType) {
                        MESSAGE_TYPE_TEXT_INDICATOR -> {
                            BluetoothMessage.Text(
                                address = address,
                                dataBuffer.toString(CHARSET_UTF_8)
                            )
                        }

                        MESSAGE_TYPE_IMAGE_INDICATOR -> {
                            BluetoothMessage.Image(
                                address = address,
                                image = dataBuffer,
                            )
                        }

                        MESSAGE_TYPE_AUDIO_INDICATOR -> {
                            BluetoothMessage.Audio(
                                address = address,
                                audio = dataBuffer,
                            )
                        }

                        else -> throw UnsupportedSchemeException("Unknown message type indicator")
                    }
                    _receivedMessageEvent.send(message)
                    isFirstChunkOfMessage = true
                    dataBuffer = null
                    dataBitsWritten = 0
                    dataLength = null
                }
            } catch (_: IOException) {
                readingJob?.cancel()
                connectionManager.closeConnection()
                break
            }
        }
    }
}
