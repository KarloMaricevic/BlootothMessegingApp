package com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.models

sealed interface AddDeviceScreenEvent {

    data object OnDiscoverableSwitchChecked : AddDeviceScreenEvent

    data object OnScanForDevicesClicked : AddDeviceScreenEvent

    data object OnBackClicked : AddDeviceScreenEvent

    data class OnDeviceClicked(val address: String) : AddDeviceScreenEvent
}
