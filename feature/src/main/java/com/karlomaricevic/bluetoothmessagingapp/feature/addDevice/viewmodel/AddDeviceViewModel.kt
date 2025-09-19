package com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.viewmodel

import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent.OnBackClicked
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent.OnDeviceClicked
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent.OnDiscoverableSwitchChecked
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent.OnScanForDevicesClicked
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenState
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.navigation.AddDeviceNavigator
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.TIMEOUT_DELAY
import com.karlomaricevic.bluetoothmessagingapp.domain.connection.models.Connection
import com.karlomaricevic.bluetoothmessagingapp.domain.connection.usecase.ConnectToServer
import com.karlomaricevic.bluetoothmessagingapp.domain.connection.usecase.GetAvailableConnections
import com.karlomaricevic.bluetoothmessagingapp.domain.connection.usecase.ObserveDiscoverableState
import com.karlomaricevic.bluetoothmessagingapp.domain.connection.usecase.ListenForConnection
import com.karlomaricevic.bluetoothmessagingapp.domain.contacts.models.Contact
import com.karlomaricevic.bluetoothmessagingapp.domain.contacts.usecase.AddContact
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent.OnDismissErrorDialogClicked
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.NewBaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddDeviceViewModel(
    private val observeDiscoverableState: ObserveDiscoverableState,
    private val getAvailableConnections: GetAvailableConnections,
    private val connectToServer: ConnectToServer,
    private val addContact: AddContact,
    private val listenForConnection: ListenForConnection,
    private val navigator: AddDeviceNavigator,
    private val ioDispatcher: CoroutineDispatcher,
    private val vmScope: CoroutineScope
) : NewBaseViewModel<AddDeviceScreenEvent>(vmScope) {

    private val isDiscoverableEnabled = MutableStateFlow(false)
    private val showStartSearchMessage = MutableStateFlow(false)
    private val bluetoothDevicesList = MutableStateFlow<List<Connection>>(listOf())
    private val showMakeDeviceVisibleError = MutableStateFlow(false)
    private val showConnectingToDeviceError = MutableStateFlow(false)

    private var waitingForClientJob: Job? = null
    private var isDeviceDiscoverableListenerJob: Job? = null

    val state = combine(
        isDiscoverableEnabled,
        showStartSearchMessage,
        bluetoothDevicesList,
        showMakeDeviceVisibleError,
        showConnectingToDeviceError,
        ::AddDeviceScreenState,
    ).stateIn(
        scope = vmScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_DELAY),
        initialValue = AddDeviceScreenState(),
    )

    override fun onEvent(event: AddDeviceScreenEvent) {
        when (event) {
            is OnDiscoverableSwitchChecked -> handleDiscoverableSwitchChanged()
            is OnScanForDevicesClicked -> {
                showStartSearchMessage.update { true }
                vmScope.launch {
                    getAvailableConnections().fold(
                        ifLeft = { /* TODO */ },
                        ifRight = { connections -> bluetoothDevicesList.update { connections } }
                    )
                }
            }
            is OnBackClicked -> launch {
                navigator.navigateUp()
            }
            is OnDeviceClicked -> launch(ioDispatcher) {
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

            OnDismissErrorDialogClicked -> {
                showConnectingToDeviceError.update { false }
                showMakeDeviceVisibleError.update { false }
            }
        }
    }

    private fun handleDiscoverableSwitchChanged() {
        isDiscoverableEnabled.update { true }
        if (waitingForClientJob == null) {
            waitingForClientJob = launch(ioDispatcher) {
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
            isDeviceDiscoverableListenerJob = launch(ioDispatcher) {
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

