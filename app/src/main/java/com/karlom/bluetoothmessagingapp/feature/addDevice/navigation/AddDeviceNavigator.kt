package com.karlom.bluetoothmessagingapp.feature.addDevice.navigation

import com.karlom.bluetoothmessagingapp.domain.connection.models.Connection

interface AddDeviceNavigator {

    suspend fun navigateUp()

    suspend fun navigateToChatScreen(connection: Connection)
}
