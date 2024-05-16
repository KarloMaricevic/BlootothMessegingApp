package com.karlom.bluetoothmessagingapp.feature.addDevice.viewmodel

import androidx.lifecycle.viewModelScope
import com.karlom.bluetoothmessagingapp.core.base.BaseViewModel
import com.karlom.bluetoothmessagingapp.domain.bluetooth.usecase.GetIsDeviceDiscoverableNotifier
import com.karlom.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent
import com.karlom.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent.OnDiscoverableSwitchChecked
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AddDeviceViewModel @Inject constructor(
    private val getIsDeviceDiscoverableNotifier: GetIsDeviceDiscoverableNotifier,
) : BaseViewModel<AddDeviceScreenEvent>() {

    val isDiscoverableEnabled = MutableStateFlow(false)

    private var bluetoothStateListenerJob: Job? = null

    override fun onEvent(event: AddDeviceScreenEvent) {
        when (event) {
            is OnDiscoverableSwitchChecked -> handleDiscoverableSwitchChanged()
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
