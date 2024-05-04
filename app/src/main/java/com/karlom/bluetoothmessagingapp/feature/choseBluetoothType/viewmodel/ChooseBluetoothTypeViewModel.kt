package com.karlom.bluetoothmessagingapp.feature.choseBluetoothType.viewmodel

import androidx.lifecycle.viewModelScope
import com.karlom.bluetoothmessagingapp.core.base.BaseViewModel
import com.karlom.bluetoothmessagingapp.core.navigation.NavigationEvent.Destination
import com.karlom.bluetoothmessagingapp.core.navigation.Navigator
import com.karlom.bluetoothmessagingapp.feature.bluetoothDevices.router.BluetoothDevicesRouter
import com.karlom.bluetoothmessagingapp.feature.choseBluetoothType.models.ChooseBluetoothTypeScreenEvent
import com.karlom.bluetoothmessagingapp.feature.choseBluetoothType.models.ChooseBluetoothTypeScreenEvent.OnSearchBluetoothDevicesClicked
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChooseBluetoothTypeViewModel @Inject constructor(
    private val navigator: Navigator,
) : BaseViewModel<ChooseBluetoothTypeScreenEvent>() {

    override fun onEvent(event: ChooseBluetoothTypeScreenEvent) {
        when (event) {
            is OnSearchBluetoothDevicesClicked -> viewModelScope.launch {
                navigator.emitDestination(Destination(BluetoothDevicesRouter.route()))
            }
        }
    }
}
