package com.karlom.bluetoothmessagingapp.feature.bluetoothDevices.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.karlom.bluetoothmessagingapp.core.base.BaseViewModel
import com.karlom.bluetoothmessagingapp.core.base.TIMEOUT_DELAY
import com.karlom.bluetoothmessagingapp.core.navigation.NavigationEvent
import com.karlom.bluetoothmessagingapp.core.navigation.Navigator
import com.karlom.bluetoothmessagingapp.domain.bluetooth.models.BluetoothDevice
import com.karlom.bluetoothmessagingapp.domain.bluetooth.usecase.GetAvailableBluetoothDevices
import com.karlom.bluetoothmessagingapp.feature.bluetoothDevices.models.BluetoothDevicesScreenEvent
import com.karlom.bluetoothmessagingapp.feature.bluetoothDevices.models.BluetoothDevicesScreenEvent.OnBluetoothDeviceClicked
import com.karlom.bluetoothmessagingapp.feature.bluetoothDevices.models.BluetoothDevicesScreenEvent.OnScanForDevicesClicked
import com.karlom.bluetoothmessagingapp.feature.bluetoothDevices.models.BluetoothDevicesScreenState
import com.karlom.bluetoothmessagingapp.feature.chat.router.ChatRouter
import dagger.hilt.android.lifecycle.HiltViewModel
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
class BluetoothDevicesViewModel @Inject constructor(
    private val getAvailableBluetoothDevices: GetAvailableBluetoothDevices,
    private val navigator: Navigator,
) : BaseViewModel<BluetoothDevicesScreenEvent>() {

    private val showSearchButton = MutableStateFlow(true)
    private val devices =
        MutableStateFlow<Flow<PagingData<BluetoothDevice>>>(flowOf(PagingData.empty()))

    val state = combine(
        showSearchButton,
        devices,
        ::BluetoothDevicesScreenState,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_DELAY),
        initialValue = BluetoothDevicesScreenState(),
    )

    override fun onEvent(event: BluetoothDevicesScreenEvent) {
        when (event) {
            is OnScanForDevicesClicked -> devices.update {
                showSearchButton.update { false }
                getAvailableBluetoothDevices().cachedIn(viewModelScope)
            }

            is OnBluetoothDeviceClicked -> viewModelScope.launch {
                navigator.emitDestination(
                    NavigationEvent.Destination(ChatRouter.creteChatRoute(event.address))
                )
            }
        }
    }
}
