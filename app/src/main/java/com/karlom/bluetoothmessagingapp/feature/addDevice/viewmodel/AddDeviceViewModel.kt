package com.karlom.bluetoothmessagingapp.feature.addDevice.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.karlom.bluetoothmessagingapp.core.base.BaseViewModel
import com.karlom.bluetoothmessagingapp.core.base.TIMEOUT_DELAY
import com.karlom.bluetoothmessagingapp.domain.bluetooth.models.BluetoothDevice
import com.karlom.bluetoothmessagingapp.domain.bluetooth.usecase.GetAvailableBluetoothDevices
import com.karlom.bluetoothmessagingapp.domain.bluetooth.usecase.GetIsDeviceDiscoverableNotifier
import com.karlom.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent
import com.karlom.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent.OnDiscoverableSwitchChecked
import com.karlom.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent.OnScanForDevicesClicked
import com.karlom.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AddDeviceViewModel @Inject constructor(
    private val getIsDeviceDiscoverableNotifier: GetIsDeviceDiscoverableNotifier,
    private val getAvailableBluetoothDevices: GetAvailableBluetoothDevices,
) : BaseViewModel<AddDeviceScreenEvent>() {

    private val isDiscoverableEnabled = MutableStateFlow(false)
    private val isBluetoothDeviceListShown = MutableStateFlow(false)
    private val bluetoothDevicesList = MutableStateFlow<Flow<PagingData<BluetoothDevice>>>(flowOf())

    val state = combine(
        isDiscoverableEnabled,
        isBluetoothDeviceListShown,
        bluetoothDevicesList,
        ::AddDeviceScreenState,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_DELAY),
        initialValue = AddDeviceScreenState(),
    )

    private var bluetoothStateListenerJob: Job? = null

    override fun onEvent(event: AddDeviceScreenEvent) {
        when (event) {
            is OnDiscoverableSwitchChecked -> handleDiscoverableSwitchChanged()
            is OnScanForDevicesClicked -> {
                isBluetoothDeviceListShown.update { true }
                bluetoothDevicesList.update { getAvailableBluetoothDevices() }
            }
        }
    }

    private fun handleDiscoverableSwitchChanged() {
        bluetoothStateListenerJob?.cancel()
        getIsDeviceDiscoverableNotifier().fold(
            { Timber.d("Error while getting discoverable notifier") },
            { notifier ->
                isDiscoverableEnabled.update { true }
                bluetoothStateListenerJob = viewModelScope.launch {
                    notifier.collect { isDiscoverable ->
                        if (!isDiscoverable) {
                            isDiscoverableEnabled.update { false }
                            cancel()
                        }
                    }
                }
            }
        )
    }
}
