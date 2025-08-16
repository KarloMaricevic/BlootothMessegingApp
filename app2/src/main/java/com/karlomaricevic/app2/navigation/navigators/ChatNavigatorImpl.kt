package com.karlomaricevic.app2.navigation.navigators

import com.karlom.bluetoothmessagingapp.feature.chat.navigation.ChatNavigator
import com.karlomaricevic.app2.navigation.NavigationEvent.NavigateBack
import com.karlomaricevic.app2.navigation.Navigator
import javax.inject.Inject

class ChatNavigatorImpl @Inject constructor(
    private val navigator: Navigator,
) : ChatNavigator {

    override suspend fun navigateBack() {
        navigator.emitDestination(NavigateBack)
    }
}
