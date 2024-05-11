package com.karlom.bluetoothmessagingapp.data.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothDevice.ACTION_FOUND
import android.bluetooth.BluetoothDevice.EXTRA_DEVICE
import android.bluetooth.BluetoothManager
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

@Singleton
class AppBluetoothManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val bluetoothManager: BluetoothManager =
        context.getSystemService(BluetoothManager::class.java)

    private val adapter: BluetoothAdapter? = bluetoothManager.adapter
    private val cachedInBluetoothDevices = HashMap<String, BluetoothDevice>()
    private var connectionInputStream: InputStream? = null
    private var connectionOutputStream: OutputStream? = null
    private var socket: Closeable? = null

    private val clientConnectedToMyServerEvent = Channel<Unit>(Channel.BUFFERED)
    private var waitingForClientJob: Job? = null

    private val inputStreamBuffer = ByteArray(1024)
    private val inputStreamChannel = Channel<ByteArray>(Channel.BUFFERED)
    private var readingStreamJob: Job? = null

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
                        val action = intent.action
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

    @SuppressLint("MissingPermission") // checked inside second condition
    suspend fun startServer(
        serviceName: String,
        serviceUUID: UUID,
    ): Either<ErrorMessage, Unit> {
        return if (adapter == null) {
            Either.Left(ErrorMessage("Device doesn't have bluetooth feature"))
        } else if (!hasPermissionsToStartOrConnectToAServer()) {
            Either.Left(ErrorMessage("Insufficient permissions to start bluetooth server"))
        } else if (socket != null) {
            Either.Left(ErrorMessage("Socket already started, close it before attempting to start a server"))
        } else {
            waitingForClientJob?.cancel()
            withContext(Dispatchers.IO) {
                try {
                    adapter.cancelDiscovery()
                    val socket = adapter.listenUsingRfcommWithServiceRecord(
                        /* name = */ serviceName,
                        /* uuid = */ serviceUUID,
                    )
                    waitingForClientJob = GlobalScope.launch(Dispatchers.IO) {
                        try {
                            val connectedSocket = socket.accept()
                            this@AppBluetoothManager.socket = socket
                            socket.close()
                            connectionInputStream = connectedSocket.inputStream
                            connectionOutputStream = connectedSocket.outputStream
                            readingStreamJob = GlobalScope.launch {
                                while (true) {
                                    connectionInputStream?.read(inputStreamBuffer)
                                    inputStreamChannel.send(inputStreamBuffer)
                                }
                            }
                            clientConnectedToMyServerEvent.send(Unit)
                        } catch (e: IOException) {
                            Timber.d("Error while waiting for client connection")
                        }
                    }
                    Either.Right(Unit)
                } catch (e: IOException) {
                    socket?.close()
                    socket = null
                    Either.Left(ErrorMessage(e.message ?: "Unknown"))
                }
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
                withContext(Dispatchers.IO) {
                    try {
                        val socket = bluetoothDevice.createRfcommSocketToServiceRecord(serviceUUID)
                        this@AppBluetoothManager.socket = socket
                        socket.connect()
                        connectionInputStream = socket.inputStream
                        connectionOutputStream = socket.outputStream
                        readingStreamJob = GlobalScope.launch {
                            while (true) {
                                connectionInputStream?.read(inputStreamBuffer)
                                inputStreamChannel.send(inputStreamBuffer)
                            }
                        }
                        Either.Right(Unit)
                    } catch (e: IOException) {
                        socket?.close()
                        socket = null
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
