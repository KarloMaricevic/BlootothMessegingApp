package com.karlom.bluetoothmessagingapp.feature.choseBluetoothType.viewmodel

import androidx.lifecycle.viewModelScope
import com.karlom.bluetoothmessagingapp.core.base.BaseViewModel
import com.karlom.bluetoothmessagingapp.core.base.TIMEOUT_DELAY
import com.karlom.bluetoothmessagingapp.core.navigation.NavigationEvent.Destination
import com.karlom.bluetoothmessagingapp.core.navigation.Navigator
import com.karlom.bluetoothmessagingapp.feature.bluetoothDevices.router.BluetoothDevicesRouter
import com.karlom.bluetoothmessagingapp.feature.choseBluetoothType.models.ChooseBluetoothTypeScreenEvent
import com.karlom.bluetoothmessagingapp.feature.choseBluetoothType.models.ChooseBluetoothTypeScreenEvent.OnMakeDiscoverableButtonClicked
import com.karlom.bluetoothmessagingapp.feature.choseBluetoothType.models.ChooseBluetoothTypeScreenEvent.OnSearchBluetoothDevicesClicked
import com.karlom.bluetoothmessagingapp.feature.choseBluetoothType.models.ChooseBluetoothTypeScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChooseBluetoothTypeViewModel @Inject constructor(
    private val navigator: Navigator,
) : BaseViewModel<ChooseBluetoothTypeScreenEvent>() {

    private val discoverButtonDisabledTime = MutableStateFlow(0)

    val state = discoverButtonDisabledTime.map { ChooseBluetoothTypeScreenState(it) }
        .stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(TIMEOUT_DELAY),
            initialValue = ChooseBluetoothTypeScreenState()
        )

    override fun onEvent(event: ChooseBluetoothTypeScreenEvent) {
        when (event) {
            is OnSearchBluetoothDevicesClicked -> viewModelScope.launch {
                navigator.emitDestination(Destination(BluetoothDevicesRouter.route()))
            }

            is OnMakeDiscoverableButtonClicked -> viewModelScope.launch {
                discoverButtonDisabledTime.update { event.discoverableTime }
                while (discoverButtonDisabledTime.value != 0) {
                    delay(1000)
                    discoverButtonDisabledTime.update { lastValue -> lastValue - 1 }
                }
            }
        }
    }
}
