package com.karlom.bluetoothmessagingapp.feature.addDevice.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.karlom.bluetoothmessagingapp.core.base.BaseViewModel
import com.karlom.bluetoothmessagingapp.core.base.TIMEOUT_DELAY
import com.karlom.bluetoothmessagingapp.domain.bluetooth.models.BluetoothDevice
import com.karlom.bluetoothmessagingapp.domain.bluetooth.usecase.GetAvailableBluetoothDevices
import com.karlom.bluetoothmessagingapp.domain.bluetooth.usecase.GetIsDeviceDiscoverableNotifier
import com.karlom.bluetoothmessagingapp.domain.bluetooth.usecase.IsServerStarted
import com.karlom.bluetoothmessagingapp.domain.chat.usecase.StartChatServer
import com.karlom.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent
import com.karlom.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent.OnDiscoverableSwitchChecked
import com.karlom.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent.OnScanForDevicesClicked
import com.karlom.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
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
    private val isServerStarted: IsServerStarted,
    private val startChatServer: StartChatServer,
) : BaseViewModel<AddDeviceScreenEvent>() {

    private val isDiscoverableEnabled = MutableStateFlow(false)
    private val isBluetoothDeviceListShown = MutableStateFlow(false)
    private val bluetoothDevicesList = MutableStateFlow<Flow<PagingData<BluetoothDevice>>>(flowOf())
    private val showMakeDeviceVisibleError = MutableStateFlow(false)

    val state = combine(
        isDiscoverableEnabled,
        isBluetoothDeviceListShown,
        bluetoothDevicesList,
        showMakeDeviceVisibleError,
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
        }
    }

    // TODO listen for discovery cancellation all the time, for now this implementation is ok
    private fun handleDiscoverableSwitchChanged() {
        if (!isServerStarted()) {
            val server = startChatServer()
            server.onLeft {
                showMakeDeviceVisibleError.update { true }
            }
            server.onRight {
                isDiscoverableEnabled.update { true }
                startListeningForDiscoverableCancellation()
            }
        } else {
            isDiscoverableEnabled.update { true }
            startListeningForDiscoverableCancellation()
        }
    }

    private fun startListeningForDiscoverableCancellation() {
        getIsDeviceDiscoverableNotifier().onRight { notifier ->
            viewModelScope.launch {
                notifier.collect {
                    isDiscoverableEnabled.update { false }
                    cancel()
                }
            }
        }
    }
}
