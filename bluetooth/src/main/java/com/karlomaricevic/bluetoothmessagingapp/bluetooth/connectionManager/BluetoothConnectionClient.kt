package com.karlomaricevic.bluetoothmessagingapp.bluetooth.connectionManager

import android.annotation.SuppressLint
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.AppBluetoothManager
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.models.SocketStreams
import com.karlomaricevic.bluetoothmessagingapp.core.common.Failure.ErrorMessage
import com.karlomaricevic.bluetoothmessagingapp.domain.connection.models.Connection
import com.karlomaricevic.bluetoothmessagingapp.platform.PermissionChecker
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.io.IOException
import java.io.OutputStream
import java.util.UUID
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class BluetoothConnectionClient(
    private val bluetoothManager: AppBluetoothManager,
    private val permissionChecker: PermissionChecker,
    private val ioDispatcher: CoroutineDispatcher,
) {

    private companion object {
        const val CONNECTION_ACCEPTED_MESSAGE = "CONN"
        const val CONNECTION_REFUSED_MESSAGE = "REFS"
    }

    private val connectedSocket = MutableStateFlow<BluetoothSocket?>(null)

    private val connectionListeners = mutableListOf<ConnectionStateListener>()

    @SuppressLint("MissingPermission")
    val connectedDevice = connectedSocket.map { socket ->
        if (socket != null) {
            Connection(
                name = socket.remoteDevice.name,
                address = socket.remoteDevice.address,
            )
        } else null
    }

    @SuppressLint("MissingPermission") // checked inside second condition
    suspend fun startServerAndWaitForConnection(
        serviceName: String,
        serviceUUID: String,
        clientAddress: String?,
        timeout: Int = -1,
    ): Either<ErrorMessage, Connection> {
        val adapter = bluetoothManager.adapter
        return if (adapter == null) {
            Left(ErrorMessage("Device doesn't have bluetooth feature"))
        } else if (!permissionChecker.hasPermissionToStartOrConnectToBtServer()) {
            Left(ErrorMessage("Insufficient permissions to start bluetooth server"))
        } else {
            return coroutineScope {
                var serverSocket: BluetoothServerSocket? = null
                try {
                    serverSocket = adapter.listenUsingRfcommWithServiceRecord(
                        serviceName,
                        UUID.fromString(serviceUUID),
                    )
                    var acceptedSocket: BluetoothSocket?
                    while (true) {
                        val candidate = withContext(ioDispatcher) {
                            serverSocket.accept(timeout)
                        }
                        if (clientAddress == null || candidate.remoteDevice.address == clientAddress) {
                            acceptedSocket = candidate
                            break
                        } else {
                            try {
                                candidate.close()
                            } catch (_: IOException) {
                            }
                        }
                    }
                    serverSocket.close()
                    acceptedSocket?.let { socket ->
                        this@BluetoothConnectionClient.connectedSocket.update { socket }
                        connectionListeners.forEach { listener ->
                            listener.onConnectionOpened(
                                address = socket.remoteDevice.address,
                                streams = SocketStreams(
                                    outputStream = socket.outputStream,
                                    inputStream = socket.inputStream,
                                )
                            )
                        }
                        Right(
                            Connection(
                                name = socket.remoteDevice.name,
                                address = socket.remoteDevice.address,
                            )
                        )
                    } ?: Left(ErrorMessage("Failed to accept connection"))
                } catch (e: IOException) {
                    try {
                        serverSocket?.close()
                    } catch (_: IOException) {
                    }
                    Left(ErrorMessage(e.message ?: "Unknown IO error"))
                } catch (e: CancellationException) {
                    try {
                        serverSocket?.close()
                    } catch (_: IOException) {
                    }
                    throw e
                }
            }
        }
    }

    @SuppressLint("MissingPermission") // checked inside second condition
    suspend fun connectToServer(
        serviceUUID: String,
        address: String,
    ): Either<ErrorMessage, Connection> {
        val adapter = bluetoothManager.adapter
        return if (adapter == null) {
            Left(ErrorMessage("Device doesn't have bluetooth feature"))
        } else if (!permissionChecker.hasPermissionToStartOrConnectToBtServer()) {
            Left(ErrorMessage("Insufficient permissions to connect to a bluetooth server"))
        } else {
            adapter.cancelDiscovery()
            return coroutineScope {
                var socket: BluetoothSocket? = null
                try {
                    val bluetoothDevice = adapter.getRemoteDevice(address)
                    socket = bluetoothDevice.createRfcommSocketToServiceRecord(
                        UUID.fromString(serviceUUID)
                    )
                    val connectedSocketDeferred = async {
                        return@async try {
                            socket.connect()
                            Right(socket)
                        } catch (e: Exception) {
                            Left(ErrorMessage(e.message ?: "Unknown"))
                        }
                    }
                    connectedSocketDeferred.await().map { connectedSocket ->
                        this@BluetoothConnectionClient.connectedSocket.update { connectedSocket }
                        connectionListeners.forEach { listener ->
                            listener.onConnectionOpened(
                                address = address,
                                streams = SocketStreams(
                                    outputStream = connectedSocket.outputStream,
                                    inputStream = connectedSocket.inputStream,
                                )
                            )
                        }
                        Connection(
                            name = bluetoothDevice.name,
                            address = bluetoothDevice.address,
                        )
                    }
                } catch (_: CancellationException) {
                    socket?.close()
                    Left(ErrorMessage("Canceled"))
                } catch (e: IOException) {
                    Left(ErrorMessage(e.message ?: "Unknown"))
                }
            }
        }
    }

    fun closeConnection() {
        val socket = connectedSocket.value
        connectionListeners.forEach { listener ->
            val socket = connectedSocket.value
            if (socket != null) {
                listener.onConnectionClosed(socket.remoteDevice.address)
            }
        }
        socket?.close()
        connectedSocket.update { null }
    }

    fun isConnectedToDevice(address: String) =
        connectedSocket.value?.remoteDevice?.address == address

    fun getOutputStream(): Either<ErrorMessage, OutputStream> {
        val outputStream = connectedSocket.value?.outputStream
        return if (outputStream == null) {
            Left(ErrorMessage("No device connected"))
        } else {
            Right(outputStream)
        }
    }

    fun registerConnectionStateListener(listener: ConnectionStateListener) {
        connectionListeners.add(listener)
    }

    @SuppressLint("MissingPermission")
    suspend fun connectToKnownDevice(
        serviceName: String,
        serviceUUID: String,
        address: String,
    ): Either<ErrorMessage, Connection> {
        val hasPermissions = permissionChecker.hasAccessToBluetoothMacAddress()
        if (!hasPermissions) {
            return Left(ErrorMessage("Insufficient permissions"))
        }
        val deviceAddress = bluetoothManager.adapter?.address
        if (deviceAddress == null) {
            return Left(ErrorMessage("Device doesn't support bt"))
        }
        return if (deviceAddress > address) {
            connectToServer(serviceUUID = serviceUUID, address = address)
        } else {
            startServerAndWaitForConnection(
                serviceName = serviceName,
                serviceUUID = serviceUUID,
                clientAddress = address,
            )
        }
    }
}
