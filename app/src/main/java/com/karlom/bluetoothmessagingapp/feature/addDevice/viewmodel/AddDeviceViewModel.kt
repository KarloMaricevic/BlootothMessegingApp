package com.karlom.bluetoothmessagingapp.feature.addDevice.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.karlom.bluetoothmessagingapp.core.base.BaseViewModel
import com.karlom.bluetoothmessagingapp.core.base.TIMEOUT_DELAY
import com.karlom.bluetoothmessagingapp.core.di.IoDispatcher
import com.karlom.bluetoothmessagingapp.core.navigation.NavigationEvent
import com.karlom.bluetoothmessagingapp.core.navigation.NavigationEvent.Destination
import com.karlom.bluetoothmessagingapp.core.navigation.Navigator
import com.karlom.bluetoothmessagingapp.domain.bluetooth.models.BluetoothDevice
import com.karlom.bluetoothmessagingapp.domain.bluetooth.usecase.GetAvailableBluetoothDevices
import com.karlom.bluetoothmessagingapp.domain.bluetooth.usecase.GetIsDeviceDiscoverableNotifier
import com.karlom.bluetoothmessagingapp.domain.chat.usecase.ConnectToServer
import com.karlom.bluetoothmessagingapp.domain.chat.usecase.StartServerAndWaitForConnection
import com.karlom.bluetoothmessagingapp.domain.contacts.models.Contact
import com.karlom.bluetoothmessagingapp.domain.contacts.usecase.AddContact
import com.karlom.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent
import com.karlom.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent.OnDeviceClicked
import com.karlom.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent.OnDiscoverableSwitchChecked
import com.karlom.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent.OnScanForDevicesClicked
import com.karlom.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenState
import com.karlom.bluetoothmessagingapp.feature.chat.router.ChatRouter
import com.karlom.bluetoothmessagingapp.feature.contacts.router.ContactsRouter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddDeviceViewModel @Inject constructor(
    private val getIsDeviceDiscoverableNotifier: GetIsDeviceDiscoverableNotifier,
    private val getAvailableBluetoothDevices: GetAvailableBluetoothDevices,
    private val connectToServer: ConnectToServer,
    private val addContact: AddContact,
    private val startServerAndWaitForConnection: StartServerAndWaitForConnection,
    private val navigator: Navigator,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : BaseViewModel<AddDeviceScreenEvent>() {

    private val isDiscoverableEnabled = MutableStateFlow(false)
    private val isBluetoothDeviceListShown = MutableStateFlow(false)
    private val bluetoothDevicesList = MutableStateFlow<Flow<PagingData<BluetoothDevice>>>(flowOf())
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
                bluetoothDevicesList.update { getAvailableBluetoothDevices() }
            }
            is AddDeviceScreenEvent.OnBackClicked -> viewModelScope.launch {
                navigator.emitDestination(NavigationEvent.NavigateUp)
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
                    navigator.emitDestination(
                        Destination(
                            destination = ChatRouter.creteChatRoute(device.address),
                            builder = {
                                popUpTo(ContactsRouter.route()) {
                                    this.inclusive = false
                                }
                            }
                        )
                    )
                }
            }
        }
    }

    private fun handleDiscoverableSwitchChanged() {
        isDiscoverableEnabled.update { true }
        if (waitingForClientJob == null) {
            waitingForClientJob = viewModelScope.launch(ioDispatcher) {
                val connection = startServerAndWaitForConnection()
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
                    navigator.emitDestination(
                        Destination(
                            destination = ChatRouter.creteChatRoute(bluetoothDevice.address),
                            builder = {
                                popUpTo(ContactsRouter.route()) {
                                    this.inclusive = false
                                }
                            }
                        )
                    )
                }
            }
        }
        if (isDeviceDiscoverableListenerJob == null) {
            isDeviceDiscoverableListenerJob = viewModelScope.launch(ioDispatcher) {
                val notifier = getIsDeviceDiscoverableNotifier()
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
