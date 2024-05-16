package com.karlom.bluetoothmessagingapp.feature.addDevice.models

sealed interface AddDeviceScreenEvent {

    data object OnDiscoverableSwitchChecked : AddDeviceScreenEvent
}
