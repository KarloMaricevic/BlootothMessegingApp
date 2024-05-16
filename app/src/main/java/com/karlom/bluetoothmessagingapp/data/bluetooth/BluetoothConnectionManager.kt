package com.karlom.bluetoothmessagingapp.data.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothServerSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.karlom.bluetoothmessagingapp.core.di.IoDispatcher
import com.karlom.bluetoothmessagingapp.core.models.Failure.ErrorMessage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

// This works for only one server in whole app, but that's fine for now
@Singleton
class BluetoothConnectionManager @Inject constructor(
    private val bluetoothManager: AppBluetoothManager,
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {

    private companion object {
        const val INPUT_BUFFER_SIZE = 1024
    }

    // this can be server or connection socket
    private var openedSocket: Closeable? = null
    private var outputStream: OutputStream? = null
    private var inputStream: InputStream? = null
    private val inputStreamBuffer = ByteArray(INPUT_BUFFER_SIZE)
    private val inputStreamChannel = Channel<ByteArray>(Channel.BUFFERED)

    private var waitingForClientJob: Job? = null
    private var readingInputStreamJob: Job? = null

    private val clientConnectedToMyServerEvent = Channel<Unit>(Channel.BUFFERED)

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("MissingPermission") // checked inside second condition
    fun startServerAndListenForConnection(
        serviceName: String,
        serviceUUID: UUID,
    ): Either<ErrorMessage, Unit> {
        val adapter = bluetoothManager.adapter
        return if (adapter == null) {
            Left(ErrorMessage("Device doesn't have bluetooth feature"))
        } else if (!hasPermissionsToStartOrConnectToAServer()) {
            Left(ErrorMessage("Insufficient permissions to start bluetooth server"))
        } else if (openedSocket != null) {
            Left(ErrorMessage("Socket already opened"))
        } else {
            try {
                val serverSocket = adapter.listenUsingRfcommWithServiceRecord(
                    /* name = */ serviceName,
                    /* uuid = */ serviceUUID,
                )
                openedSocket = serverSocket
                waitingForClientJob = GlobalScope.launch(ioDispatcher) {
                    listenForOneConnectionThenClose(serverSocket)
                }
                Right(Unit)
            } catch (e: IOException) {
                Left(ErrorMessage(e.message ?: "Unknown"))
            }
        }
    }

    @SuppressLint("MissingPermission") // checked inside second condition
    suspend fun connectToServer(serviceUUID: UUID, address: String): Either<ErrorMessage, Unit> {
        val adapter = bluetoothManager.adapter
        return if (adapter == null) {
            Left(ErrorMessage("Device doesn't have bluetooth feature"))
        } else if (!hasPermissionsToStartOrConnectToAServer()) {
            Left(ErrorMessage("Insufficient permissions to connect to a bluetooth server"))
        } else {
            adapter.cancelDiscovery()
            val bluetoothDevice = adapter.getRemoteDevice(address)
            withContext(ioDispatcher) {
                try {
                    val socket = bluetoothDevice.createRfcommSocketToServiceRecord(serviceUUID)
                    openedSocket = socket
                    socket.connect()
                    outputStream = socket.outputStream
                    inputStream = socket.inputStream
                    Right(Unit)
                } catch (e: IOException) {
                    openedSocket?.close()
                    Left(ErrorMessage(e.message ?: "Unknown"))
                }
            }
        }
    }

    fun isServerStarted() = openedSocket is BluetoothServerSocket

    fun closeConnection() {
        readingInputStreamJob?.cancel()
        waitingForClientJob?.cancel()
        outputStream = null
        inputStream = null
        openedSocket?.close()
    }

    suspend fun send(bytes: ByteArray): Either<ErrorMessage, Unit> =
        if (outputStream == null) {
            Left(ErrorMessage("Not connected with anyone"))
        } else {
            withContext(ioDispatcher) {
                try {
                    outputStream?.write(bytes)
                    Right(Unit)
                } catch (error: IOException) {
                    Left(ErrorMessage(error.message ?: "Unknown"))
                }
            }
        }

    fun getDataReceiverFlow() =
        if (inputStream == null) {
            Left(ErrorMessage("Not connected with anyone"))

        } else {
            Right(inputStreamChannel.consumeAsFlow())
        }

    fun getClientConnectedMyServerNotifier() = clientConnectedToMyServerEvent.consumeAsFlow()

    private suspend fun listenForOneConnectionThenClose(socket: BluetoothServerSocket) {
        try {
            val connectedSocket = socket.accept()
            socket.close()
            openedSocket = connectedSocket
            outputStream = connectedSocket.outputStream
            inputStream = connectedSocket.inputStream
            startReadingInputStream(connectedSocket.inputStream)
            clientConnectedToMyServerEvent.send(Unit)
        } catch (e: IOException) {
            Timber.d(e.message)
            listenForOneConnectionThenClose(socket)
        }
    }

    private fun hasPermissionsToStartOrConnectToAServer(): Boolean {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Manifest.permission.BLUETOOTH_CONNECT
        } else {
            Manifest.permission.BLUETOOTH
        }
        return ActivityCompat.checkSelfPermission(
            /* context = */ context,
            /* permission = */ permission,
        ) == PackageManager.PERMISSION_GRANTED
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun startReadingInputStream(inputStream: InputStream) {
        readingInputStreamJob = GlobalScope.launch(ioDispatcher) {
            while (true) {
                try {
                    inputStream.read(inputStreamBuffer)
                    inputStreamChannel.send(inputStreamBuffer)
                } catch (_: IOException) {
                    // TODO handle this, probably with closing connection and prompting user to connect again
                    Timber.d("Error reading input stream")
                }
            }
        }
    }
}
