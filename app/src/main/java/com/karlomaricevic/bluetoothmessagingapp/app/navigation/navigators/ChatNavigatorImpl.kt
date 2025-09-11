package com.karlomaricevic.bluetoothmessagingapp.app.navigation.navigators

import com.karlomaricevic.bluetoothmessagingapp.app.navigation.NavigationEvent.NavigateBack
import com.karlomaricevic.bluetoothmessagingapp.app.navigation.Navigator
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.navigation.ChatNavigator

class ChatNavigatorImpl(private val navigator: Navigator) : ChatNavigator {

    override suspend fun navigateBack() {
        navigator.emitDestination(NavigateBack)
    }
}
