package com.karlomaricevic.bluetoothmessagingapp.feature2.contacts.navigation

interface ContactsNavigator {

    suspend fun navigateToAddDeviceScreen()

    suspend fun navigateToChatScreen(contactName: String, address: String)
}
