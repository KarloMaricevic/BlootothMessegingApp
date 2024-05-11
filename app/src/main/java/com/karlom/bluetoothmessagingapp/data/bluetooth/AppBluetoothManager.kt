package com.karlom.bluetoothmessagingapp.data.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.ACTION_FOUND
import android.bluetooth.BluetoothDevice.EXTRA_DEVICE
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import arrow.core.Either
import com.karlom.bluetoothmessagingapp.core.models.Failure.ErrorMessage
import com.karlom.bluetoothmessagingapp.data.bluetooth.models.BluetoothDeviceResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

// TODO Separate this to multiple classes (e.g. ConnectionManager), for now ok
@Singleton
class AppBluetoothManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val adapter: BluetoothAdapter? =
        context.getSystemService(BluetoothManager::class.java)?.adapter
    private val cachedInBluetoothDevices = HashMap<String, BluetoothDevice>()
    private var openedSocket: Closeable? = null
    private var connectionOutputStream: OutputStream? = null
    private val clientConnectedToMyServerEvent = Channel<Unit>(Channel.BUFFERED)
    private val inputStreamBuffer = ByteArray(1024)
    private val inputStreamChannel = Channel<ByteArray>(Channel.BUFFERED)
    private var readingStreamJob: Job? = null
    private var waitingForClientJob: Job? = null

    @SuppressLint("MissingPermission") // checked inside first method call
    suspend fun getAvailableBluetoothDevices(): Either<ErrorMessage, List<BluetoothDeviceResponse>> =
        if (adapter == null) {
            Either.Left(ErrorMessage("Device doesn't have bluetooth feature"))
        } else if (!hasPermissionsToStartBluetoothDiscovery()) {
            Either.Left(ErrorMessage("Insufficient permissions to start bluetooth discovery"))
        } else {
            suspendCancellableCoroutine<Either<ErrorMessage, List<BluetoothDeviceResponse>>> { continuation ->
                val discoveredDevices = mutableListOf<BluetoothDeviceResponse>()
                val receiver = object : BroadcastReceiver() {

                    override fun onReceive(context: Context, intent: Intent) {
                        val action = intent.action.orEmpty()
                        when (action) {
                            ACTION_FOUND -> {
                                val device: BluetoothDevice? =
                                    intent.getParcelableExtra(EXTRA_DEVICE)
                                device?.let {
                                    cachedInBluetoothDevices[device.address] = device
                                    discoveredDevices.add(
                                        BluetoothDeviceResponse(
                                            name = device.name,
                                            address = device.address,
                                        )
                                    )
                                }
                            }

                            ACTION_DISCOVERY_FINISHED -> {
                                context.unregisterReceiver(this)
                                continuation.resume(Either.Right(discoveredDevices))
                            }
                        }
                    }
                }
                context.registerReceiver(receiver, IntentFilter(ACTION_FOUND))
                context.registerReceiver(receiver, IntentFilter(ACTION_DISCOVERY_FINISHED))
                continuation.invokeOnCancellation { context.unregisterReceiver(receiver) }
                val isDiscoveryStarted = adapter.startDiscovery()
                if (!isDiscoveryStarted) {
                    context.unregisterReceiver(receiver)
                    continuation.resume(Either.Left(ErrorMessage("Cant start discovery")))
                }
            }
        }

    private fun hasPermissionsToStartBluetoothDiscovery(): Boolean {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(Manifest.permission.BLUETOOTH_SCAN)
        } else {
            listOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        }
        permissions.forEach { permission ->
            if (ActivityCompat.checkSelfPermission(
                    context, permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    // TODO This method can only listen one socket at the time, for now it's ok because we only listen for messaging socket
    @SuppressLint("MissingPermission") // checked inside second condition
    fun startServer(
        serviceName: String,
        serviceUUID: UUID,
    ): Either<ErrorMessage, Unit> {
        return if (adapter == null) {
            Either.Left(ErrorMessage("Device doesn't have bluetooth feature"))
        } else if (!hasPermissionsToStartOrConnectToAServer()) {
            Either.Left(ErrorMessage("Insufficient permissions to start bluetooth server"))
        } else if (openedSocket != null && openedSocket is BluetoothSocket) {
            Either.Left(ErrorMessage("Client socket opened"))
        } else if (openedSocket != null && openedSocket is BluetoothServerSocket) {
            Timber.d("Server socket already opened")
            Either.Right(Unit)
        } else {
            waitingForClientJob?.cancel()
            readingStreamJob?.cancel()
            try {
                val serverSocket = adapter.listenUsingRfcommWithServiceRecord(
                    /* name = */ serviceName,
                    /* uuid = */ serviceUUID,
                )
                openedSocket = serverSocket
                listenForOneConnectionThenClose(serverSocket)
                Either.Right(Unit)
            } catch (e: IOException) {
                Either.Left(ErrorMessage(e.message ?: "Unknown"))
            }
        }
    }

    private fun listenForOneConnectionThenClose(socket: BluetoothServerSocket) {
        waitingForClientJob?.cancel()
        waitingForClientJob = GlobalScope.launch(Dispatchers.IO) {
            try {
                val connectedSocket = socket.accept()
                socket.close()
                connectionOutputStream = connectedSocket.outputStream
                startReadingInputStream(connectedSocket.inputStream)
                clientConnectedToMyServerEvent.send(Unit)
            } catch (e: IOException) {
                Timber.d(e.message)
                listenForOneConnectionThenClose(socket)
            }
        }
    }

    @SuppressLint("MissingPermission") // checked inside second condition
    suspend fun connectToServer(serviceUUID: UUID, address: String) =
        if (adapter == null) {
            Either.Left(ErrorMessage("Device doesn't have bluetooth feature"))
        } else if (!hasPermissionsToStartOrConnectToAServer()) {
            Either.Left(ErrorMessage("Insufficient permissions to connect to a bluetooth server"))
        } else {
            val bluetoothDevice = cachedInBluetoothDevices[address]
            if (bluetoothDevice == null) {
                Either.Left(ErrorMessage("Unknown MAC address"))
            } else {
                adapter.cancelDiscovery()
                readingStreamJob?.cancel()
                withContext(Dispatchers.IO) {
                    try {
                        val socket = bluetoothDevice.createRfcommSocketToServiceRecord(serviceUUID)
                        this@AppBluetoothManager.openedSocket = socket
                        socket.connect()
                        connectionOutputStream = socket.outputStream
                        startReadingInputStream(socket.inputStream)
                        Either.Right(Unit)
                    } catch (e: IOException) {
                        openedSocket?.close()
                        openedSocket = null
                        Either.Left(ErrorMessage(e.message ?: "Unknown"))
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

    private fun startReadingInputStream(inputStream: InputStream) {
        readingStreamJob = GlobalScope.launch {
            while (true) {
                try {
                    inputStream.read(inputStreamBuffer)
                    inputStreamChannel.send(inputStreamBuffer)
                } catch (_: IOException) {
                    Timber.d("Error reading input stream")
                }
            }
        }
    }

    fun getClientConnectedMyServerEventFlow(): Flow<Unit> =
        clientConnectedToMyServerEvent.consumeAsFlow()

    suspend fun send(bytes: ByteArray) = suspendCoroutine { continuation ->
        if (connectionOutputStream == null) {
            continuation.resume(Either.Left(ErrorMessage("Not connected with anyone")))
        }
        try {
            connectionOutputStream?.write(bytes)
            continuation.resume(Either.Right(Unit))
        } catch (error: IOException) {
            continuation.resume(Either.Left(ErrorMessage(error.message ?: "Unknown")))
        }
    }

    fun getDataReceiverFlow() = inputStreamChannel.consumeAsFlow()
}
