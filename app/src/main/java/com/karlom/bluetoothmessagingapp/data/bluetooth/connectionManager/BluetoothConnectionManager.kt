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
import com.karlom.bluetoothmessagingapp.data.bluetooth.communicationMenager.errorDispatcher.CommunicationErrorDispatcher
import com.karlom.bluetoothmessagingapp.data.bluetooth.models.SocketConnection
import com.karlom.bluetoothmessagingapp.domain.bluetooth.models.BluetoothDevice
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.io.Closeable
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class BluetoothConnectionManager @Inject constructor(
    private val bluetoothManager: AppBluetoothManager,
    private val errorDispatcher: CommunicationErrorDispatcher,
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ConnectionNotifier {


    private val connectedSockets = MutableStateFlow<List<BluetoothSocket>>(listOf())

    @SuppressLint("MissingPermission")
    val connectedDevices = connectedSockets.map { sockets ->
        sockets.map { socket ->
            BluetoothDevice(
                socket.remoteDevice.name,
                socket.remoteDevice.address
            )
        }
    }

    private val _connectionNotifier = Channel<SocketConnection>(24, BufferOverflow.DROP_OLDEST)
    override val connectedDeviceNotifier = _connectionNotifier.consumeAsFlow()

    init {
        GlobalScope.launch(ioDispatcher) {
            errorDispatcher.errorEvent.collect { address ->
                connectedSockets.value.firstOrNull { item -> item.remoteDevice.address == address }
                    ?.let { socketInError ->
                        socketInError.close()
                        _connectionNotifier.trySend(SocketConnection(socketInError, false))
                        connectedSockets.update { it - socketInError }
                    }
            }
        }
    }

    @SuppressLint("MissingPermission") // checked inside second condition
    suspend fun startServerAndWaitForConnection(
        serviceName: String,
        serviceUUID: UUID,
    ): Either<ErrorMessage, BluetoothDevice> {
        val adapter = bluetoothManager.adapter
        return if (adapter == null) {
            Left(ErrorMessage("Device doesn't have bluetooth feature"))
        } else if (!hasPermissionsToStartOrConnectToAServer()) {
            Left(ErrorMessage("Insufficient permissions to start bluetooth server"))
        } else {
            coroutineScope {
                var serverSocket: BluetoothServerSocket? = null
                try {
                    serverSocket = adapter.listenUsingRfcommWithServiceRecord(
                        /* name = */ serviceName,
                        /* uuid = */ serviceUUID,
                    )
                    val connectedSocketDeferred = async {
                        return@async try {
                            Right(serverSocket.accept())
                        } catch (e: IOException) {
                            Left(ErrorMessage(e.message ?: "Unknown"))
                        }
                    }
                    val connectedSocket = connectedSocketDeferred.await()
                    serverSocket.close()
                    when (connectedSocket) {
                        is Right -> {
                            connectedSockets.update { it + connectedSocket.value }
                            _connectionNotifier.send(SocketConnection(connectedSocket.value, true))
                            return@coroutineScope Right(
                                BluetoothDevice(
                                    connectedSocket.value.remoteDevice.name,
                                    connectedSocket.value.remoteDevice.address,
                                )
                            )
                        }

                        is Left -> return@coroutineScope connectedSocket
                    }
                } catch (e: CancellationException) {
                    serverSocket?.close()
                    Left(ErrorMessage("Canceled"))
                } catch (e: IOException) {
                    Left(ErrorMessage(e.message ?: "Unknown"))
                }
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
            return coroutineScope {
                var socket: BluetoothSocket? = null
                try {
                    val bluetoothDevice = adapter.getRemoteDevice(address)
                    socket = bluetoothDevice.createRfcommSocketToServiceRecord(serviceUUID)
                    val connectedSocketDeferred = async {
                        return@async try {
                            socket.connect()
                            Right(socket)
                        } catch (e: IOException) {
                            Left(ErrorMessage(e.message ?: "Unknown"))
                        }
                    }
                    val connectedSocket = connectedSocketDeferred.await()
                    return@coroutineScope when (connectedSocket) {
                        is Right -> {
                            val domainBluetoothDevice = BluetoothDevice(
                                name = bluetoothDevice.name,
                                address = bluetoothDevice.address,
                            )
                            connectedSockets.update { it + socket }
                            _connectionNotifier.send(SocketConnection(socket, true))
                            Right(domainBluetoothDevice)
                        }

                        is Left -> connectedSocket
                    }
                } catch (e: CancellationException) {
                    socket?.close()
                    Left(ErrorMessage("Canceled"))
                } catch (e: IOException) {
                    Left(ErrorMessage(e.message ?: "Unknown"))
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

    fun closeAllConnections() {
        connectedSockets.value.forEach { socket ->
            _connectionNotifier.trySend(
                SocketConnection(
                    bluetoothSocket = socket,
                    isConnected = false
                )
            )
            socket.close()
        }
    }

    fun isConnectedToDevice(address: String) =
        connectedSockets.value.firstOrNull { it.remoteDevice.address == address } != null
}
