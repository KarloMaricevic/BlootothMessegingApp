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
import com.karlom.bluetoothmessagingapp.domain.connection.models.Connection
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

    private val connectedSocket = MutableStateFlow<BluetoothSocket?>(null)

    private val connectionListeners = mutableListOf<ConnectionStateListener>()

    @SuppressLint("MissingPermission")
    val connectedDevice = connectedSocket.map { socket ->
        if (socket != null) {
            Connection(
                socket.remoteDevice.name,
                socket.remoteDevice.address
            )
        } else null
    }

    @SuppressLint("MissingPermission") // checked inside second condition
    suspend fun startServerAndWaitForConnection(
        serviceName: String,
        serviceUUID: UUID,
    ): Either<ErrorMessage, Connection> {
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
                    connectedSocket.map { socket ->
                        this@BluetoothConnectionManager.connectedSocket.update { socket }
                        connectionListeners.forEach {
                            it.onConnectionOpened(
                                address = socket.remoteDevice.address,
                                streams = SocketStreams(
                                    outputStream = socket.outputStream,
                                    inputStream = socket.inputStream,
                                )
                            )
                        }
                        Connection(
                            socket.remoteDevice.name,
                            socket.remoteDevice.address,
                        )
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
    ): Either<ErrorMessage, Connection> {
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
                    val connectedSocketResult = connectedSocketDeferred.await()
                    connectedSocketResult.map { connectedSocket ->
                        this@BluetoothConnectionManager.connectedSocket.update { connectedSocket }
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

    fun closeConnection() {
        val socket = connectedSocket.value
        connectionListeners.forEach { listener ->
            val socket = connectedSocket.value
            if(socket != null) {
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
}
