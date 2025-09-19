package com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.models.AddDeviceScreenEvent
import kotlinx.coroutines.CoroutineScope

class AndroidAddDeviceViewModel(vmFactory: (CoroutineScope) -> AddDeviceViewModel) : ViewModel() {

    private val sharedVM: AddDeviceViewModel = vmFactory(viewModelScope)

    val state = sharedVM.state

    fun onEvent(event: AddDeviceScreenEvent) {
        sharedVM.onEvent(event)
    }
}