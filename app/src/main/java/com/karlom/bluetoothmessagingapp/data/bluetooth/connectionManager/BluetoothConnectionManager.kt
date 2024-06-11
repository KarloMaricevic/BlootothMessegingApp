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
import com.karlom.bluetoothmessagingapp.core.models.Failure.ErrorMessage
import com.karlom.bluetoothmessagingapp.data.bluetooth.AppBluetoothManager
import com.karlom.bluetoothmessagingapp.data.bluetooth.models.SocketStreams
import com.karlom.bluetoothmessagingapp.domain.bluetooth.models.BluetoothDevice
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.io.IOException
import java.io.OutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BluetoothConnectionManager @Inject constructor(
    private val bluetoothManager: AppBluetoothManager,
    @ApplicationContext private val context: Context,
) {

    private val connectedSockets = MutableStateFlow<List<BluetoothSocket>>(listOf())

    private val connectionListeners = mutableListOf<ConnectionStateListener>()

    @SuppressLint("MissingPermission")
    val connectedDevices = connectedSockets.map { sockets ->
        sockets.map { socket ->
            BluetoothDevice(
                socket.remoteDevice.name,
                socket.remoteDevice.address
            )
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
                            connectionListeners.forEach {
                                it.onConnectionOpened(
                                    address = connectedSocket.value.remoteDevice.address,
                                    streams = SocketStreams(
                                        outputStream = connectedSocket.value.outputStream,
                                        inputStream = connectedSocket.value.inputStream,
                                    )
                                )
                            }
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
                            connectionListeners.forEach {
                                it.onConnectionOpened(
                                    address = address,
                                    streams = SocketStreams(
                                        outputStream = connectedSocket.value.outputStream,
                                        inputStream = connectedSocket.value.inputStream,
                                    )
                                )
                            }
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
        connectionListeners.forEach { listener ->
            connectedSockets.value.forEach { socket ->
                listener.onConnectionClosed(socket.remoteDevice.address)
            }
        }
        connectedSockets.value.forEach { socket -> socket.close() }
        connectedSockets.update { listOf() }
    }

    fun closeConnection(address: String) {
        val socket = connectedSockets.value.firstOrNull { it.remoteDevice.address == address }
        socket?.close()
        connectedSockets.update { sockets -> sockets.toMutableList().apply { remove(socket) } }
        connectionListeners.forEach { listener -> listener.onConnectionClosed(address) }
    }

    fun isConnectedToDevice(address: String) =
        connectedSockets.value.firstOrNull { it.remoteDevice.address == address } != null

    fun getOutputStream(address: String): Either<ErrorMessage, OutputStream> {
        val inputStream =
            connectedSockets.value.firstOrNull { it.remoteDevice.address == address }?.outputStream
        return if (inputStream == null) {
            Left(ErrorMessage("No device connected with that address"))
        } else {
            Right(inputStream)
        }
    }

    fun registerConnectionStateListener(listener: ConnectionStateListener) {
        connectionListeners.add(listener)
    }
}
