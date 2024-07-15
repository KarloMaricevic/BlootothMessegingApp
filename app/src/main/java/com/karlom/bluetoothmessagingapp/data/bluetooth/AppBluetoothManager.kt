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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class AppBluetoothManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    val adapter: BluetoothAdapter? =
        context.getSystemService(BluetoothManager::class.java)?.adapter

    @SuppressLint("MissingPermission") // checked inside first method call
    suspend fun getAvailableBluetoothDevices(): Either<ErrorMessage, List<BluetoothDeviceResponse>> =
        if (adapter == null) {
            Either.Left(ErrorMessage("Device doesn't have bluetooth feature"))
        } else if (!hasPermissionsToStartBluetoothDiscovery()) {
            Either.Left(ErrorMessage("Insufficient permissions to start bluetooth discovery"))
        } else {
            suspendCancellableCoroutine<Either<ErrorMessage, List<BluetoothDeviceResponse>>> { continuation ->
                val discoveredDevices = hashMapOf<String, BluetoothDeviceResponse>()
                val receiver = object : BroadcastReceiver() {

                    override fun onReceive(context: Context, intent: Intent) {
                        val action = intent.action.orEmpty()
                        when (action) {
                            ACTION_FOUND -> {
                                val device: BluetoothDevice? =
                                    intent.getParcelableExtra(EXTRA_DEVICE)
                                device?.let {
                                    discoveredDevices.put(
                                        device.address,
                                        BluetoothDeviceResponse(
                                            name = device.name ?: "No name",
                                            address = device.address,
                                        )
                                    )
                                }
                            }

                            ACTION_DISCOVERY_FINISHED -> {
                                context.unregisterReceiver(this)
                                continuation.resume(Either.Right(discoveredDevices.values.toList()))
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

    fun getIsDeviceDiscoverableNotifier(): Either<ErrorMessage, Flow<Boolean>> =
        if (adapter == null) {
            Either.Left(ErrorMessage("Device doesn't have bluetooth feature"))
        } else if (!hasPermissionToListenForBtChanges()) {
            Either.Left(ErrorMessage("Insufficient permissions for listening for bt changes"))
        } else {
            Either.Right(callbackFlow {
                val receiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        if (intent?.action == BluetoothAdapter.ACTION_SCAN_MODE_CHANGED) {
                            val currentMode = intent.getIntExtra(
                                BluetoothAdapter.EXTRA_SCAN_MODE,
                                BluetoothAdapter.SCAN_MODE_NONE
                            )
                            trySend(currentMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
                        }
                    }
                }
                context.registerReceiver(
                    /* receiver = */ receiver,
                    /* filter = */ IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)
                )
                awaitClose { context.unregisterReceiver(receiver) }
            }
            )
        }

    private fun hasPermissionToListenForBtChanges(): Boolean {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Manifest.permission.BLUETOOTH_SCAN
        } else {
            Manifest.permission.BLUETOOTH_ADMIN
        }
        return ActivityCompat.checkSelfPermission(
            /* context = */ context,
            /* permission = */ permission,
        ) == PackageManager.PERMISSION_GRANTED
    }
}
