package com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.viewmodel

import androidx.lifecycle.viewModelScope
import com.karlomaricevic.bluetoothmessagingapp.dispatchers.IoDispatcher
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent.OnBackClicked
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent.OnDeviceClicked
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent.OnDiscoverableSwitchChecked
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent.OnScanForDevicesClicked
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenState
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.navigation.AddDeviceNavigator
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.BaseViewModel
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.TIMEOUT_DELAY
import com.karlomaricevic.domain.connection.models.Connection
import com.karlomaricevic.domain.connection.usecase.ConnectToServer
import com.karlomaricevic.domain.connection.usecase.GetAvailableConnections
import com.karlomaricevic.domain.connection.usecase.ObserveDiscoverableState
import com.karlomaricevic.domain.connection.usecase.ListenForConnection
import com.karlomaricevic.domain.contacts.models.Contact
import com.karlomaricevic.domain.contacts.usecase.AddContact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddDeviceViewModel @Inject constructor(
    private val observeDiscoverableState: ObserveDiscoverableState,
    private val getAvailableConnections: GetAvailableConnections,
    private val connectToServer: ConnectToServer,
    private val addContact: AddContact,
    private val listenForConnection: ListenForConnection,
    private val navigator: AddDeviceNavigator,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : BaseViewModel<AddDeviceScreenEvent>() {

    private val isDiscoverableEnabled = MutableStateFlow(false)
    private val isBluetoothDeviceListShown = MutableStateFlow(false)
    private val bluetoothDevicesList = MutableStateFlow<List<Connection>?>(null)
    private val showMakeDeviceVisibleError = MutableStateFlow(false)
    private val showConnectingToDeviceError = MutableStateFlow(false)

    private var waitingForClientJob: Job? = null
    private var isDeviceDiscoverableListenerJob: Job? = null

    val state = combine(
        isDiscoverableEnabled,
        isBluetoothDeviceListShown,
        bluetoothDevicesList,
        showMakeDeviceVisibleError,
        showConnectingToDeviceError,
        ::AddDeviceScreenState,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_DELAY),
        initialValue = AddDeviceScreenState(),
    )

    override fun onEvent(event: AddDeviceScreenEvent) {
        when (event) {
            is OnDiscoverableSwitchChecked -> handleDiscoverableSwitchChanged()
            is OnScanForDevicesClicked -> {
                isBluetoothDeviceListShown.update { true }
                viewModelScope.launch {
                    getAvailableConnections().fold(
                        ifLeft = { /* TODO */ },
                        ifRight = { connections -> bluetoothDevicesList.update { connections } }
                    )
                }
            }
            is OnBackClicked -> viewModelScope.launch {
                navigator.navigateUp()
            }
            is OnDeviceClicked -> viewModelScope.launch(ioDispatcher) {
                val connectToServer = connectToServer(event.address)
                connectToServer.onLeft { showConnectingToDeviceError.update { true } }
                connectToServer.onRight { device ->
                    addContact(
                        Contact(
                            name = device.name,
                            address = device.address,
                        )
                    )
                    navigator.navigateToChatScreen(device)
                }
            }
        }
    }

    private fun handleDiscoverableSwitchChanged() {
        isDiscoverableEnabled.update { true }
        if (waitingForClientJob == null) {
            waitingForClientJob = viewModelScope.launch(ioDispatcher) {
                val connection = listenForConnection()
                connection.onLeft {
                    waitingForClientJob = null
                    isDiscoverableEnabled.update { false }
                }
                connection.onRight { bluetoothDevice ->
                    addContact(
                        Contact(
                            name = bluetoothDevice.name,
                            address = bluetoothDevice.address,
                        )
                    )
                    navigator.navigateToChatScreen(bluetoothDevice)
                }
            }
        }
        if (isDeviceDiscoverableListenerJob == null) {
            isDeviceDiscoverableListenerJob = viewModelScope.launch(ioDispatcher) {
                val notifier = observeDiscoverableState()
                notifier.onLeft {
                    isDeviceDiscoverableListenerJob = null
                    isDiscoverableEnabled.update { false }
                }
                notifier.onRight {
                    it.collect { isDiscoverable ->
                        if (isDiscoverable && waitingForClientJob != null) {
                            isDiscoverableEnabled.update { true }
                        } else {
                            isDiscoverableEnabled.update { false }
                        }
                    }
                }
            }
        }
    }
}
