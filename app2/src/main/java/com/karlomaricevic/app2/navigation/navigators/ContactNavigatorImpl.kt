package com.karlomaricevic.app2.navigation.navigators

import com.karlomaricevic.app2.navigation.NavigationEvent.Destination
import com.karlomaricevic.app2.navigation.Navigator
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.navigation.AddDeviceScreenRouter
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.navigation.ChatRouter
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.navigation.ContactsNavigator
import javax.inject.Inject

class ContactNavigatorImpl @Inject constructor(
    private val navigator: Navigator
) : ContactsNavigator {

    override suspend fun navigateToAddDeviceScreen(
    ) {
        navigator.emitDestination(
            Destination(
                AddDeviceScreenRouter.route()
            )
        )
    }

    override suspend fun navigateToChatScreen(
        contactName: String,
        address: String,
    ) {
        navigator.emitDestination(
            Destination(
                ChatRouter.creteChatRoute(
                    contactName = contactName,
                    address = address,
                )
            )
        )
    }
}