package com.karlom.bluetoothmessagingapp.data.bluetooth.communicationMenager

import android.media.UnsupportedSchemeException
import arrow.core.Either
import com.karlom.bluetoothmessagingapp.core.di.IoDispatcher
import com.karlom.bluetoothmessagingapp.core.models.Failure
import com.karlom.bluetoothmessagingapp.data.bluetooth.communicationMenager.errorDispatcher.CommunicationErrorDispatcher
import com.karlom.bluetoothmessagingapp.data.bluetooth.connectionManager.ConnectionNotifier
import com.karlom.bluetoothmessagingapp.data.bluetooth.models.BluetoothMessage
import com.karlom.bluetoothmessagingapp.data.bluetooth.models.CommunicationEntry
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
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
    private val errorDispatcher: CommunicationErrorDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    connectionNotifier: ConnectionNotifier,
) {

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

    private val communicationEntries = mutableMapOf<String, CommunicationEntry>()

    init {
        GlobalScope.launch(ioDispatcher) {
            connectionNotifier.connectedDeviceNotifier.collect { socket ->
                if (socket.isConnected) {
                    communicationEntries[socket.bluetoothSocket.remoteDevice.address] =
                        CommunicationEntry(
                            socket = socket.bluetoothSocket,
                            readingJob = startReadingInputStream(
                                inputStream = socket.bluetoothSocket.inputStream,
                                address = socket.bluetoothSocket.remoteDevice.address,
                            )
                        )
                } else {
                    val entry = communicationEntries[socket.bluetoothSocket.remoteDevice.address]
                    entry?.socket?.inputStream?.close()
                    entry?.socket?.outputStream?.close()
                    communicationEntries.remove(socket.bluetoothSocket.remoteDevice.address)
                }
            }
        }
    }

    suspend fun sendText(
        text: String,
        address: String,
    ): Either<Failure.ErrorMessage, Unit> {
        val outputStream = communicationEntries[address]?.socket?.outputStream
        return if (outputStream == null) {
            Either.Left(Failure.ErrorMessage("Not connected with device"))
        } else {
            withContext(ioDispatcher) {
                try {
                    val dataArray = text.toByteArray(CHARSET_UTF_8)
                    val sizeBuffer = ByteBuffer.allocate(Int.SIZE_BYTES).putInt(dataArray.size)
                    val typeArray =
                        ByteBuffer.allocate(Int.SIZE_BYTES).putInt(MESSAGE_TYPE_TEXT_INDICATOR)
                    outputStream.write(sizeBuffer.array())
                    outputStream.write(typeArray.array())
                    outputStream.write(dataArray)
                    Either.Right(Unit)
                } catch (error: IOException) {
                    Either.Left(Failure.ErrorMessage(error.message ?: "Unknown"))
                }
            }
        }
    }

    suspend fun sendImage(
        stream: InputStream,
        streamSize: Int,
        address: String,
    ): Either<Failure.ErrorMessage, Unit> {
        val outputStream = communicationEntries[address]?.socket?.outputStream
        return if (outputStream == null)
            Either.Left(Failure.ErrorMessage("Not connected with device"))
        else {
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
                    outputStream.write(sizeArray)
                    outputStream.write(typeArray)
                    while (stream.read(dataBuffer).also { bytesRead = it } != -1) {
                        outputStream.write(dataBuffer, 0, bytesRead)
                    }
                    Either.Right(Unit)
                } catch (e: Exception) {
                    closeCommunication(address)
                    errorDispatcher.notify(address)
                    Either.Left(Failure.ErrorMessage(e.message ?: "Unknown"))
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
                if (isFirstChunkOfMessage) {
                    dataLength =
                        bytesToInt(inputBuffer.copyOfRange(0, DATA_SIZE_PREFIX_SIZE))
                    dataType = bytesToInt(
                        inputBuffer.copyOfRange(
                            DATA_SIZE_PREFIX_SIZE,
                            DATA_SIZE_PREFIX_SIZE + MESSAGE_TYPE_PREFIX_SIZE
                        )
                    )
                    dataBuffer = ByteArray(dataLength.toInt())
                }
                System.arraycopy(
                    inputBuffer,
                    if (isFirstChunkOfMessage) DATA_SIZE_PREFIX_SIZE + MESSAGE_TYPE_PREFIX_SIZE else 0,
                    dataBuffer!!,
                    dataBitsWritten,
                    if (isFirstChunkOfMessage) {
                        if (inputBuffer.size - DATA_SIZE_PREFIX_SIZE + MESSAGE_TYPE_PREFIX_SIZE > dataLength!!) {
                            dataLength
                        } else {
                            inputBuffer.size - DATA_SIZE_PREFIX_SIZE + MESSAGE_TYPE_PREFIX_SIZE
                        }
                    } else {
                        if (dataBitsWritten + inputBuffer.size < dataLength!!) {
                            inputBuffer.size
                        } else {
                            dataLength - dataBitsWritten
                        }
                    }
                )
                dataBitsWritten += bytesRead - if (isFirstChunkOfMessage) DATA_SIZE_PREFIX_SIZE + MESSAGE_TYPE_PREFIX_SIZE else 0
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
                closeCommunication(address)
                errorDispatcher.notify(address)
                break
            }
        }
    }

    private fun bytesToInt(bytes: ByteArray): Int {
        require(bytes.size == 4) { "Byte array must be of length 4 to convert to Int" }
        return (bytes[0].toInt() and 0xFF shl 24) or
                (bytes[1].toInt() and 0xFF shl 16) or
                (bytes[2].toInt() and 0xFF shl 8) or
                (bytes[3].toInt() and 0xFF)
    }

    private fun closeCommunication(address: String) {
        val entry = communicationEntries[address]
        entry?.readingJob?.cancel()
        entry?.socket?.inputStream?.close()
        entry?.socket?.outputStream?.close()
        communicationEntries.remove(address)
    }
}