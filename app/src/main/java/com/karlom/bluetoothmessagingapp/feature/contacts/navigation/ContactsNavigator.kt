package com.karlom.bluetoothmessagingapp.feature.contacts.navigation

interface ContactsNavigator {

    suspend fun navigateToAddDeviceScreen()

    suspend fun navigateToChatScreen(contactName: String, address: String)
}
