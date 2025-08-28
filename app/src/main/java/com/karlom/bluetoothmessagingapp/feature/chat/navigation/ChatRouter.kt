package com.karlom.bluetoothmessagingapp.feature.chat.navigation

import com.karlomaricevic.core.navigation.NavigationDestination

object ChatRouter : NavigationDestination {

    private const val CHAT_SCREEN_ROOT = "chat"
    const val ADDRESS_PARAM = "address"
    const val CONTACT_NAME_PARAM = "contactName"

    private const val DETAILS_SCREEN_ROUTE =
        "$CHAT_SCREEN_ROOT/{$CONTACT_NAME_PARAM}/{$ADDRESS_PARAM}"

    override fun route() = DETAILS_SCREEN_ROUTE

    fun creteChatRoute(contactName: String, address: String) =
        "$CHAT_SCREEN_ROOT/$contactName/$address"
}
