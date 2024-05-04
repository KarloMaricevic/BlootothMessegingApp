package com.karlom.bluetoothmessagingapp.feature.choseBluetoothType.models

sealed interface ChooseBluetoothTypeScreenEvent {

    data object OnSearchBluetoothDevicesClicked : ChooseBluetoothTypeScreenEvent
}
