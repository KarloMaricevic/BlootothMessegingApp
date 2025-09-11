package com.karlomaricevic.app2.navigation.navigators

import com.karlomaricevic.app2.navigation.NavigationEvent.NavigateUp
import com.karlomaricevic.app2.navigation.NavigationEvent.Destination
import com.karlomaricevic.app2.navigation.Navigator
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.navigation.AddDeviceNavigator
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.navigation.ChatRouter
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.navigation.ContactsRouter
import com.karlomaricevic.domain.connection.models.Connection

class AddDeviceNavigatorImpl(private val navigator: Navigator): AddDeviceNavigator {

    override suspend fun navigateUp() {
        navigator.emitDestination(NavigateUp)
    }

    override suspend fun navigateToChatScreen(connection: Connection) {
        navigator.emitDestination(
            Destination(
                destination = ChatRouter.creteChatRoute(
                    contactName = connection.name,
                    address = connection.address,
                ),
                builder = {
                    popUpTo(ContactsRouter.route()) {
                        this.inclusive = false
                    }
                }
            )
        )
    }
}
