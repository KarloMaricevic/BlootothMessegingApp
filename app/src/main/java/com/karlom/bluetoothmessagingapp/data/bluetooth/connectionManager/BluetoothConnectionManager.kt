package com.karlom.bluetoothmessagingapp.data.bluetooth.connectionManager

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.karlom.bluetoothmessagingapp.core.di.IoDispatcher
import com.karlom.bluetoothmessagingapp.core.models.Failure.ErrorMessage
import com.karlom.bluetoothmessagingapp.data.bluetooth.AppBluetoothManager
import com.karlom.bluetoothmessagingapp.data.bluetooth.models.ConnectionState
import com.karlom.bluetoothmessagingapp.data.bluetooth.models.ConnectionState.Connected
import com.karlom.bluetoothmessagingapp.data.bluetooth.models.ConnectionState.NotConnected
import com.karlom.bluetoothmessagingapp.domain.bluetooth.models.BluetoothDevice
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.Closeable
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

// This works for only one server in whole app, but that's fine for now
@Singleton
class BluetoothConnectionManager @Inject constructor(
    private val bluetoothManager: AppBluetoothManager,
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ConnectionNotifier {

    // this can be server or connection socket
    private var listeningSocket: Closeable? = null

    @Volatile
    private var connectedSocket: Closeable? = null

    private var internalConnectionStateNotifier =
        Channel<BluetoothSocket?>(capacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private var connectionState = MutableStateFlow<ConnectionState>(NotConnected)

    private var waitingForClientJob: Job? = null

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
        } else if (listeningSocket != null) {
            Left(ErrorMessage("Socket already opened"))
        } else {
            try {
                val serverSocket = adapter.listenUsingRfcommWithServiceRecord(
                    /* name = */ serviceName,
                    /* uuid = */ serviceUUID,
                )
                listeningSocket = serverSocket
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
    suspend fun connectToServer(
        serviceUUID: UUID,
        address: String,
    ): Either<ErrorMessage, BluetoothDevice> {
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
                    synchronized(this) {
                        if (connectedSocket == null) {
                            socket.connect()
                            connectedSocket = socket
                            val domainBluetoothDevice = BluetoothDevice(
                                name = bluetoothDevice.name,
                                address = bluetoothDevice.address,
                            )
                            internalConnectionStateNotifier.trySend(socket)
                            connectionState.update { Connected(domainBluetoothDevice) }
                            Right(domainBluetoothDevice)
                        } else {
                            Left(ErrorMessage("Already connected to some device"))
                        }
                    }
                } catch (e: IOException) {
                    connectedSocket?.close()
                    Left(ErrorMessage(e.message ?: "Unknown"))
                }
            }
        }
    }

    suspend fun startListeningAndTryToConnectToSpecificDevice(
        serviceName: String,
        serviceUUID: UUID,
        address: String,
    ): Either<ErrorMessage, BluetoothDevice> {
        closeConnection()
        startServerAndListenForConnection(
            serviceName,
            serviceUUID,
        )
        val isConnected = connectToServer(serviceUUID, address)
        isConnected.onRight { closeListeningServer() }
        return isConnected
    }

    fun isServerStarted() = connectedSocket is BluetoothServerSocket

    fun closeConnection() {
        waitingForClientJob?.cancel()
        listeningSocket?.close()
        connectedSocket?.close()
        connectionState.update { NotConnected }
    }

    private suspend fun closeListeningServer() {
        waitingForClientJob?.cancel()
        listeningSocket?.close()
        waitingForClientJob?.join()
        waitingForClientJob = null
        listeningSocket = null
    }

    fun getClientConnectedMyServerNotifier() = clientConnectedToMyServerEvent.consumeAsFlow()

    fun getConnectionState() = connectionState.asStateFlow()

    @SuppressLint("MissingPermission")
    private suspend fun listenForOneConnectionThenClose(socket: BluetoothServerSocket) {
        coroutineScope {
            try {
                val connectedSocket = socket.accept()
                socket.close()
                synchronized(this) {
                    if (this@BluetoothConnectionManager.connectedSocket == null) {
                        this@BluetoothConnectionManager.connectedSocket = connectedSocket
                        internalConnectionStateNotifier.trySend(connectedSocket)
                        connectionState.update {
                            Connected(
                                BluetoothDevice(
                                    name = connectedSocket.remoteDevice.name,
                                    address = connectedSocket.remoteDevice.address,
                                )
                            )
                        }
                        clientConnectedToMyServerEvent.trySend(Unit)
                    } else {
                        Timber.d("Device is already connected")
                        connectedSocket.close()
                    }
                }
            } catch (e: IOException) {
                if (!this.isActive) {
                    Timber.d("Exception thrown in closed listening coroutine")
                } else {
                    Timber.d("Unexpected error occurred while listening for client")
                    listenForOneConnectionThenClose(socket)
                }
            }
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

    override fun getNotifier(): Flow<BluetoothSocket?> =
        internalConnectionStateNotifier.consumeAsFlow()
}
