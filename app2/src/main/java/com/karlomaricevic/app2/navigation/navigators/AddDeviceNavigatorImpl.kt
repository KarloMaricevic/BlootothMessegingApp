package com.karlomaricevic.app2.navigation.navigators

import com.karlom.bluetoothmessagingapp.feature.addDevice.navigation.AddDeviceNavigator
import com.karlom.bluetoothmessagingapp.feature.chat.navigation.ChatRouter
import com.karlom.bluetoothmessagingapp.feature.contacts.navigation.ContactsRouter
import com.karlomaricevic.app2.navigation.NavigationEvent.NavigateUp
import com.karlomaricevic.app2.navigation.NavigationEvent.Destination
import com.karlomaricevic.app2.navigation.Navigator
import com.karlomaricevic.domain.connection.models.Connection
import javax.inject.Inject

class AddDeviceNavigatorImpl @Inject constructor(
    private val navigator: Navigator,
): AddDeviceNavigator {

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
