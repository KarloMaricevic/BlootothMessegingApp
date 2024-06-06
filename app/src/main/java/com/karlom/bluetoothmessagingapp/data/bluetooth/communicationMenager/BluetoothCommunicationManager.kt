package com.karlom.bluetoothmessagingapp.data.bluetooth.communicationMenager

import arrow.core.Either
import com.karlom.bluetoothmessagingapp.core.di.IoDispatcher
import com.karlom.bluetoothmessagingapp.core.models.Failure
import com.karlom.bluetoothmessagingapp.data.bluetooth.communicationMenager.errorDispatcher.CommunicationErrorDispatcher
import com.karlom.bluetoothmessagingapp.data.bluetooth.connectionManager.ConnectionNotifier
import com.karlom.bluetoothmessagingapp.data.bluetooth.models.CommunicationEntry
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
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
        const val LENGTH_PREFIX_SIZE = Int.SIZE_BYTES
        const val CHUNK_SIZE = 1024
    }

    private val _receivedMessageEvent = Channel<Pair<String, ByteArray>>(Channel.BUFFERED)
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

    suspend fun send(
        bytes: ByteArray,
        address: String,
    ): Either<Failure.ErrorMessage, Unit> {
        val outputStream = communicationEntries[address]?.socket?.outputStream
        return if (outputStream == null) {
            Either.Left(Failure.ErrorMessage("Not connected with anyone"))
        } else {
            withContext(ioDispatcher) {
                try {
                    val sizeBuffer = ByteBuffer.allocate(Int.SIZE_BYTES)
                    sizeBuffer.putInt(bytes.size)
                    outputStream.write(sizeBuffer.array())
                    outputStream.write(bytes)
                    Either.Right(Unit)
                } catch (error: IOException) {
                    Either.Left(Failure.ErrorMessage(error.message ?: "Unknown"))
                }
            }
        }
    }

    suspend fun send(
        stream: InputStream,
        streamSize: Long,
        address: String,
    ): Either<Failure.ErrorMessage, Unit> {
        val outputStream = communicationEntries[address]?.socket?.outputStream
        return if (outputStream == null)
            Either.Left(Failure.ErrorMessage("Not connected with anyone"))
        else {
            try {
                val sizeBuffer = ByteBuffer.allocate(LENGTH_PREFIX_SIZE)
                val dataBuffer = ByteArray(CHUNK_SIZE)
                var bytesRead: Int
                sizeBuffer.putInt(streamSize.toInt())
                outputStream.write(sizeBuffer.array())
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

    @OptIn(DelicateCoroutinesApi::class)
    private fun startReadingInputStream(
        inputStream: InputStream,
        address: String,
    ) = GlobalScope.launch(ioDispatcher) {
        val inputBuffer = ByteArray(CHUNK_SIZE)
        var dataBuffer: ByteArray? = null
        var dataBitsWritten = 0
        var dataLength: Int? = null
        var isFirstChunkOfMessage = true
        while (true) {
            try {
                val bytesRead = inputStream.read(inputBuffer)
                if (isFirstChunkOfMessage) {
                    dataLength =
                        bytesToInt(inputBuffer.copyOfRange(0, LENGTH_PREFIX_SIZE))
                    dataBuffer = ByteArray(dataLength.toInt())
                }
                System.arraycopy(
                    inputBuffer,
                    if (isFirstChunkOfMessage) LENGTH_PREFIX_SIZE else 0,
                    dataBuffer!!,
                    dataBitsWritten,
                    if (isFirstChunkOfMessage) {
                        if (inputBuffer.size - LENGTH_PREFIX_SIZE > dataLength!!) {
                            dataLength
                        } else {
                            inputBuffer.size - LENGTH_PREFIX_SIZE
                        }
                    } else {
                        if (dataBitsWritten + inputBuffer.size < dataLength!!) {
                            inputBuffer.size
                        } else {
                            dataLength - dataBitsWritten
                        }
                    }
                )
                dataBitsWritten += bytesRead - if (isFirstChunkOfMessage) LENGTH_PREFIX_SIZE else 0
                if (isFirstChunkOfMessage) {
                    isFirstChunkOfMessage = false
                }
                if (dataLength == dataBitsWritten) {
                    _receivedMessageEvent.send(Pair(address, dataBuffer))
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
