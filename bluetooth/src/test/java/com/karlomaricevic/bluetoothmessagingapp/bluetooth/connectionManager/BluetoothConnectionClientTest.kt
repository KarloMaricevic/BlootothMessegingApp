package com.karlomaricevic.bluetoothmessagingapp.bluetooth.connectionManager

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.AppBluetoothManager
import com.karlomaricevic.bluetoothmessagingapp.domain.connection.models.Connection
import com.karlomaricevic.bluetoothmessagingapp.platform.PermissionChecker
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import java.io.IOException
import java.util.UUID
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class BluetoothConnectionClientTest {

    private val testDispatcher = StandardTestDispatcher()
    private val bluetoothManager = mockk<AppBluetoothManager>()
    private val permissionChecker = mockk<PermissionChecker>()
    private val bluetoothAdapter = mockk<BluetoothAdapter>()
    private val btDevice = mockk<BluetoothDevice>()
    private val btSocket = mockk<BluetoothSocket>()
    private val serverSocket = mockk<BluetoothServerSocket>()


    val sut = BluetoothConnectionClient(
        bluetoothManager = bluetoothManager,
        permissionChecker = permissionChecker,
        ioDispatcher = testDispatcher,
    )

    @Test
    fun connectToServer_NoBluetoothAdapter_ReturnsError() = runTest(testDispatcher) {
        every { bluetoothManager.adapter } returns null

        val result = sut.connectToServer(serviceUUID = "", address = "")

        result.shouldBeLeft()
    }

    @Test
    fun connectToServer_InsufficientPermissions_ReturnsError() = runTest(testDispatcher) {
        every { permissionChecker.hasPermissionToStartOrConnectToBtServer() } returns false
        every { bluetoothManager.adapter } returns bluetoothAdapter

        val result = sut.connectToServer(serviceUUID = "", address = "")

        result.shouldBeLeft()
    }

    @Test
    fun connectToServer_ValidInput_ReturnsConnectionAndNotifiesListeners() = runTest(testDispatcher) {
        val address = "address"
        val deviceName = "deviceName"
        val serviceUUID = UUID.randomUUID()
        every { bluetoothManager.adapter } returns bluetoothAdapter
        every { permissionChecker.hasPermissionToStartOrConnectToBtServer() } returns true
        every { bluetoothAdapter.cancelDiscovery() } returns true
        every { bluetoothAdapter.getRemoteDevice(address) } returns btDevice
        every { btDevice.name } returns deviceName
        every { btDevice.address } returns address
        every { btDevice.createRfcommSocketToServiceRecord(serviceUUID) } returns btSocket
        coEvery { btSocket.connect() } just Runs
        every { btSocket.outputStream } returns mockk(relaxed = true)
        every { btSocket.inputStream } returns mockk(relaxed = true)
        val listener = mockk<ConnectionStateListener>(relaxed = true)
        sut.registerConnectionStateListener(listener)

        val result = sut.connectToServer(serviceUUID.toString(), address)

        result.shouldBeRight(
            Connection(
                name = deviceName,
                address = address,
            )
        )

        coVerifyOrder {
            bluetoothAdapter.cancelDiscovery()
            btSocket.connect()
        }
        verify { listener.onConnectionOpened(address, any()) }
    }

    @Test
    fun startServerAndWaitForConnection_NoAdapter_ReturnsError() = runTest(testDispatcher) {
        every { bluetoothManager.adapter } returns null

        val result = sut.startServerAndWaitForConnection(
            serviceName = "",
            serviceUUID = UUID.randomUUID().toString(),
            clientAddress = null,
        )

        result.shouldBeLeft()
    }

    @Test
    fun startServerAndWaitForConnection_NoPermission_ReturnsError() = runTest(testDispatcher) {
        every { bluetoothManager.adapter } returns bluetoothAdapter
        every { permissionChecker.hasPermissionToStartOrConnectToBtServer() } returns false


        val result = sut.startServerAndWaitForConnection(
            serviceName = "",
            serviceUUID = UUID.randomUUID().toString(),
            clientAddress = null
        )

        result.shouldBeLeft()
    }

    @Test
    fun startServerAndWaitForConnection_AcceptThrowsIOException_ReturnsError() = runTest(testDispatcher) {
        every { bluetoothManager.adapter } returns bluetoothAdapter
        every { permissionChecker.hasPermissionToStartOrConnectToBtServer() } returns true
        every { bluetoothAdapter.listenUsingRfcommWithServiceRecord(any(), any()) } returns serverSocket
        every { serverSocket.close() } just Runs
        coEvery { serverSocket.accept(-1) } throws IOException("boom")

        val result = sut.startServerAndWaitForConnection(
            serviceName = "",
            serviceUUID = UUID.randomUUID().toString(),
            clientAddress = null,
        )

        testScheduler.advanceUntilIdle()

        result.shouldBeLeft()
    }

    @Test
    fun startServerAndWaitForConnection_ValidInput_ReturnsConnectionAndNotifiesListeners() = runTest(testDispatcher) {
        val address = "address"
        val deviceName = "deviceName"
        val serviceUUID = UUID.randomUUID()

        every { bluetoothManager.adapter } returns bluetoothAdapter
        every { permissionChecker.hasPermissionToStartOrConnectToBtServer() } returns true
        every { bluetoothAdapter.listenUsingRfcommWithServiceRecord(any(), any()) } returns serverSocket
        every { serverSocket.close() } just Runs
        every { btSocket.remoteDevice } returns btDevice
        every { btDevice.name } returns deviceName
        every { btDevice.address } returns address
        every { btSocket.outputStream } returns mockk(relaxed = true)
        every { btSocket.inputStream } returns mockk(relaxed = true)
        coEvery { serverSocket.accept(-1) } returns btSocket

        val listener = mockk<ConnectionStateListener>(relaxed = true)
        sut.registerConnectionStateListener(listener)

        val result = sut.startServerAndWaitForConnection(
            serviceName = "",
            serviceUUID = serviceUUID.toString(),
            clientAddress = address,
        )
        testScheduler.advanceUntilIdle()

        result.shouldBeRight(
            Connection(
                name = deviceName,
                address = address,
            )
        )
        coVerify { serverSocket.accept(-1) }
        coVerify { serverSocket.close() }
        verify { listener.onConnectionOpened(address, any()) }
    }

    @Test
    fun startServerAndWaitForConnection_AcceptTimeout_ThrowsIOException_ReturnsError() = runTest(testDispatcher) {
        val serviceUUID = UUID.randomUUID()
        every { bluetoothManager.adapter } returns bluetoothAdapter
        every { permissionChecker.hasPermissionToStartOrConnectToBtServer() } returns true
        every { bluetoothAdapter.listenUsingRfcommWithServiceRecord(any(), any()) } returns serverSocket
        every { serverSocket.close() } just Runs
        coEvery { serverSocket.accept(any()) } throws IOException("timeout")

        val result = sut.startServerAndWaitForConnection(
            serviceName = "",
            serviceUUID = serviceUUID.toString(),
            clientAddress = null,
            timeout = 1000,
        )

        testScheduler.advanceUntilIdle()

        result.shouldBeLeft()
        coVerify { serverSocket.accept(1000) }
        coVerify { serverSocket.close() }
    }

    @Test
    fun startServerAndWaitForConnection_WrongClientThenCorrectClient_ReturnsConnection() = runTest(testDispatcher) {
        val wrongAddress = "wrongAddress"
        val correctAddress = "correctAddress"
        val deviceName = "deviceName"
        val serviceUUID = UUID.randomUUID()
        every { bluetoothManager.adapter } returns bluetoothAdapter
        every { permissionChecker.hasPermissionToStartOrConnectToBtServer() } returns true
        every { bluetoothAdapter.listenUsingRfcommWithServiceRecord(any(), any()) } returns serverSocket
        every { serverSocket.close() } just Runs
        val wrongSocket = mockk<BluetoothSocket>()
        val correctSocket = mockk<BluetoothSocket>()
        val wrongDevice = mockk<BluetoothDevice>()
        val correctDevice = mockk<BluetoothDevice>()
        every { wrongSocket.remoteDevice } returns wrongDevice
        every { wrongDevice.address } returns wrongAddress
        every { wrongSocket.close() } just Runs
        every { correctSocket.remoteDevice } returns correctDevice
        every { correctDevice.name } returns deviceName
        every { correctDevice.address } returns correctAddress
        every { correctSocket.outputStream } returns mockk(relaxed = true)
        every { correctSocket.inputStream } returns mockk(relaxed = true)
        coEvery { serverSocket.accept(-1) } returnsMany listOf(wrongSocket, correctSocket)
        val listener = mockk<ConnectionStateListener>(relaxed = true)
        sut.registerConnectionStateListener(listener)

        val result = sut.startServerAndWaitForConnection(
            serviceName = "",
            serviceUUID = serviceUUID.toString(),
            clientAddress = correctAddress,
        )

        testScheduler.advanceUntilIdle()

        result.shouldBeRight(
            Connection(
                name = deviceName,
                address = correctAddress,
            )
        )
        coVerify(exactly = 2) { serverSocket.accept(-1) }
        verify { wrongSocket.close() }
        verify { listener.onConnectionOpened(correctAddress, any()) }
    }
}
