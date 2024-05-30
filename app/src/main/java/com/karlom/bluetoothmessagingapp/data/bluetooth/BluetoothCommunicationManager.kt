package com.karlom.bluetoothmessagingapp.data.bluetooth

import arrow.core.Either
import com.karlom.bluetoothmessagingapp.core.di.IoDispatcher
import com.karlom.bluetoothmessagingapp.core.models.Failure
import com.karlom.bluetoothmessagingapp.data.bluetooth.connectionManager.ConnectionNotifier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BluetoothCommunicationManager @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    connectionNotifier: ConnectionNotifier,
) {

    private companion object {
        const val LENGTH_PREFIX_SIZE = Int.SIZE_BYTES
        const val CHUNK_SIZE = 1024
    }

    private var outputStream: OutputStream? = null
    private var inputStream: InputStream? = null
    private val inputStreamBuffer = ByteArray(CHUNK_SIZE)
    private val _receivedMessageEvent = Channel<ByteArray>(Channel.BUFFERED)
    val receivedMessageEvent = _receivedMessageEvent.receiveAsFlow()

    private var readingInputStreamJob: Job? = null

    init {
        GlobalScope.launch(ioDispatcher) {
            connectionNotifier.getNotifier().collect { socket ->
                readingInputStreamJob?.cancel()
                outputStream = socket?.outputStream
                inputStream = socket?.inputStream
                if (socket?.inputStream != null) {
                    startReadingInputStream(socket.inputStream)
                }
            }
        }
    }

    suspend fun send(bytes: ByteArray): Either<Failure.ErrorMessage, Unit> =
        if (outputStream == null) {
            Either.Left(Failure.ErrorMessage("Not connected with anyone"))
        } else {
            withContext(ioDispatcher) {
                try {
                    val sizeBuffer = ByteBuffer.allocate(Int.SIZE_BYTES)
                    sizeBuffer.putInt(bytes.size)
                    outputStream?.write(sizeBuffer.array())
                    outputStream?.write(bytes)
                    Either.Right(Unit)
                } catch (error: IOException) {
                    Either.Left(Failure.ErrorMessage(error.message ?: "Unknown"))
                }
            }
        }

    suspend fun send(stream: InputStream, streamSize: Long) =
        if (outputStream == null)
            Either.Left(Failure.ErrorMessage("Not connected with anyone"))
        else {
            try {
                val sizeBuffer = ByteBuffer.allocate(LENGTH_PREFIX_SIZE)
                val dataBuffer = ByteArray(CHUNK_SIZE)
                var bytesRead: Int
                sizeBuffer.putInt(streamSize.toInt())
                outputStream?.write(sizeBuffer.array())
                while (stream.read(dataBuffer).also { bytesRead = it } != -1) {
                    outputStream?.write(dataBuffer, 0, bytesRead)
                }
                Either.Right(Unit)
            } catch (e: Exception) {
                Either.Left(Failure.ErrorMessage(e.message ?: "Unknown"))
            }
        }

    @OptIn(DelicateCoroutinesApi::class)
    private fun startReadingInputStream(inputStream: InputStream) {
        readingInputStreamJob = GlobalScope.launch(ioDispatcher) {
            var receivedDataBuffer: ByteArray? = null
            var dataBitsWritten = 0
            var dataLength: Int? = null
            var isFirstChunkOfMessage = true
            while (true) {
                try {
                    val bytesRead = inputStream.read(inputStreamBuffer)
                    if (isFirstChunkOfMessage) {
                        dataLength =
                            bytesToInt(inputStreamBuffer.copyOfRange(0, LENGTH_PREFIX_SIZE))
                        receivedDataBuffer = ByteArray(dataLength.toInt())
                    }
                    System.arraycopy(
                        inputStreamBuffer,
                        if (isFirstChunkOfMessage) LENGTH_PREFIX_SIZE else 0,
                        receivedDataBuffer!!,
                        dataBitsWritten,
                        if (isFirstChunkOfMessage) {
                            if (inputStreamBuffer.size - LENGTH_PREFIX_SIZE > dataLength!!) {
                                dataLength
                            } else {
                                inputStreamBuffer.size - LENGTH_PREFIX_SIZE
                            }
                        } else {
                            if (dataBitsWritten + inputStreamBuffer.size < dataLength!!) {
                                inputStreamBuffer.size
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
                        _receivedMessageEvent.send(receivedDataBuffer)
                        isFirstChunkOfMessage = true
                        receivedDataBuffer = null
                        dataBitsWritten = 0
                        dataLength = null
                    }
                } catch (_: IOException) {
                    // TODO handle this, probably with closing connection and prompting user to connect again
                    Timber.d("Error reading input stream")
                }
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
}
