package com.karlom.bluetoothmessagingapp.data.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import com.karlom.bluetoothmessagingapp.data.bluetooth.connectionManager.BluetoothConnectionManager
import com.karlom.bluetoothmessagingapp.data.bluetooth.connectionManager.ConnectionStateListener
import com.karlom.bluetoothmessagingapp.data.bluetooth.models.SocketStreams
import com.karlom.bluetoothmessagingapp.data.shared.utils.PermissionChecker
import com.karlom.bluetoothmessagingapp.domain.connection.models.Connection
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.shouldBe
import io.mockk.Called
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import io.mockk.verifyOrder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class BluetoothConnectionManagerTest {

    private val ioTestDispatcher = Dispatchers.IO

    val btManager = mockk<AppBluetoothManager>(relaxed = true)
    val btAdapter = mockk<BluetoothAdapter>(relaxed = true)
    val btDevice = mockk<BluetoothDevice>()
    val permissionChecker = mockk<PermissionChecker>(relaxed = true)
    val btServerSocket = mockk<BluetoothServerSocket>(relaxed = true)
    val btSocket = mockk<BluetoothSocket>(relaxed = true)

    val uuid = UUID.randomUUID()
    val serviceName = ""
    val deviceName = ""
    val outputStream = ByteArrayOutputStream()
    val inputStream = ByteArrayInputStream(byteArrayOf())
    val timeout = 100000

    val connectionListeners = listOf(
        mockk<ConnectionStateListener>(),
        mockk<ConnectionStateListener>(),
    )
    val address = "address"
    val unknownAddress = "unknownAddress"
    val errorMessage = "ERROR_MESSAGE"
    private val sut = BluetoothConnectionManager(
        bluetoothManager = btManager,
        permissionChecker = permissionChecker,
        ioDispatcher = ioTestDispatcher,
    )

    @Before
    fun setUp() {
        every { btManager.adapter } returns btAdapter
        every { btAdapter.getRemoteDevice(address) } returns btDevice
        connectionListeners.forEach { listener ->
            every { listener.onConnectionOpened(any(), any()) } just runs
            sut.registerConnectionStateListener(listener)
        }
        every { btSocket.remoteDevice } returns btDevice
        every { btSocket.outputStream } returns outputStream
        every { btSocket.inputStream } returns inputStream
        every { btDevice.address } returns address
        every { btDevice.name } returns deviceName
    }

    @Test
    fun connectToServer_EstablishConnection() = runTest {
        every { permissionChecker.hasPermissionToStartOrConnectToBtServer() } returns true
        every { btDevice.createRfcommSocketToServiceRecord(uuid) } returns btSocket
        every { btSocket.connect() } just runs
        val expected = Connection(deviceName, address)

        val result = sut.connectToServer(uuid, address)

        connectionListeners.forEach { listener ->
            val capturedStreams = slot<SocketStreams>()

            verify(exactly = 1) {
                listener.onConnectionOpened(
                    address = address,
                    streams = capture(capturedStreams)
                )
            }
            capturedStreams.captured.outputStream shouldBe outputStream
            capturedStreams.captured.inputStream shouldBe inputStream
        }
        verifyOrder {
            btAdapter.cancelDiscovery()
            btSocket.connect()
        }
        result shouldBeRight expected
    }

    @Test
    fun connectToServer_InsufficientPermissions_ErrorMessage() = runTest {
        every { permissionChecker.hasPermissionToStartOrConnectToBtServer() } returns false
        every { btDevice.createRfcommSocketToServiceRecord(uuid) } returns btSocket
        every { btSocket.connect() } just runs

        val result = sut.connectToServer(uuid,address)

        verify(exactly = 0) {
            btSocket.connect()
        }
        result.shouldBeLeft()
    }

    @Test
    fun connectToServer_ErrorWhileConnecting_ErrorMessage() = runTest {
        every { permissionChecker.hasPermissionToStartOrConnectToBtServer() } returns true
        every { btDevice.createRfcommSocketToServiceRecord(uuid) } returns btSocket
        every { btSocket.connect() } throws Exception()

        val result = sut.connectToServer(uuid,address)

        result.shouldBeLeft()
    }

    @Test
    fun startServerAndWaitForConnection_AnyClientCanConnect_EstablishConnectionAndNotifyListeners() = runTest {
        every { permissionChecker.hasPermissionToStartOrConnectToBtServer() } returns true
        every { btAdapter.listenUsingRfcommWithServiceRecord(serviceName, uuid) } returns btServerSocket
        every { btServerSocket.accept(timeout) } returns btSocket
        every { btServerSocket.close() } returns Unit
        val expected = Connection(
            name = deviceName,
            address = address,
        )

        val result = sut.startServerAndWaitForConnection(
            serviceName = serviceName,
            serviceUUID = uuid,
            timeout = timeout,
        )

        connectionListeners.forEach { listener ->
            val capturedStreams = slot<SocketStreams>()

            verify(exactly = 1) {
                listener.onConnectionOpened(
                    address = address,
                    streams = capture(capturedStreams)
                )
            }

            capturedStreams.captured.outputStream shouldBe outputStream
            capturedStreams.captured.inputStream shouldBe inputStream
        }
        result shouldBeRight expected
    }

    @Test
    fun startServerAndWaitForConnection_NoPermissionToOpenServer_ErrorMessage() = runTest {
        every { permissionChecker.hasPermissionToStartOrConnectToBtServer() } returns false

        val result = sut.startServerAndWaitForConnection(serviceName, uuid)

        result.shouldBeLeft()
    }

    @Test
    fun startServerAndWaitForConnection_SocketServerFailsToOpen_ErrorMessage() = runTest {
        every { permissionChecker.hasPermissionToStartOrConnectToBtServer() } returns true
        every { btAdapter.listenUsingRfcommWithServiceRecord(serviceName, uuid) } throws IOException()

        val result = sut.startServerAndWaitForConnection(serviceName, uuid)

        result.shouldBeLeft()
    }

    @Test
    fun startServerAndWaitForSpecificConnection_UnspecifiedClient_DenyConnection() = runTest {
        every { permissionChecker.hasPermissionToStartOrConnectToBtServer() } returns true
        every { btAdapter.listenUsingRfcommWithServiceRecord(serviceName, uuid) } returns btServerSocket
        every { btDevice.address } returns unknownAddress
        var calls = 0
        every { btServerSocket.accept(timeout) } coAnswers {
            if (++calls == 1) {
                btSocket
            } else {
                suspendCancellableCoroutine { /* never resumes */ }
            }
        }
        every { btServerSocket.close() } returns Unit
        async(ioTestDispatcher) {
            sut.startServerAndWaitForConnection(serviceName, uuid, address, timeout)
        }


        connectionListeners.forEach { listener ->
            val capturedStreams = slot<SocketStreams>()
            verify(exactly = 0) {
                listener.onConnectionOpened(
                    address = address,
                    streams = capture(capturedStreams)
                )
            }
            capturedStreams.captured.outputStream shouldBe null
            capturedStreams.captured.inputStream shouldBe null
        }
        verify { btServerSocket.close() wasNot Called }
        verify { btSocket.close() }
    }

    // TODO this test isn't passing, problem is that accept finishes before calling cancelAndJoin()
/*    @Test
    fun startServerAndWaitForConnection_CallerCancels_CloseOpenedSocket() = runTest {
        every { permissionChecker.hasPermissionToStartOrConnectToBtServer() } returns true
        every { btAdapter.listenUsingRfcommWithServiceRecord(serviceName, uuid) } returns btServerSocket
        every { btServerSocket.accept() } coAnswers {
            delay(100000)
            btSocket
        }
        every { btServerSocket.close() } returns Unit

        val job = launch { sut.startServerAndWaitForConnection(serviceName, uuid) }
        advanceTimeBy(200)
        job.cancelAndJoin()
        // delay(3000)
        //   val i = job.getCompletionExceptionOrNull()
        // val j = job.getCompleted()
        // val k = job.

        job.isCancelled shouldBe true
        // job.isCancelled shouldBe true
        //job.getCompletionExceptionOrNull() shouldBe CancellationException::class
        // job.isCancelled shouldBe true
        verify { btServerSocket.close() }
    }*/
}