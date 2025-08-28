package com.karlom.bluetoothmessagingapp.feature.addDevice.navigation

import com.karlomaricevic.domain.connection.models.Connection

interface AddDeviceNavigator {

    suspend fun navigateUp()

    suspend fun navigateToChatScreen(connection: Connection)
}
