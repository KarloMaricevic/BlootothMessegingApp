package com.karlom.bluetoothmessagingapp.feature.choseBluetoothType.router

import com.karlom.bluetoothmessagingapp.core.navigation.NavigationDestination

object ChooseBluetoothTypeRouter : NavigationDestination {

    private const val CHOOSE_BLUETOOTH_SCREEN_ROUTE = "assignedIssues"

    override fun route() = CHOOSE_BLUETOOTH_SCREEN_ROUTE
}
