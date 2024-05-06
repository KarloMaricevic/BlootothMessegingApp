package com.karlom.bluetoothmessagingapp.feature.chat.router

import com.karlom.bluetoothmessagingapp.core.navigation.NavigationDestination

object ChatRouter : NavigationDestination {

    private const val CHAT_SCREEN_ROOT = "chat"
    const val ADDRESS_PARAM = "address"

    private const val DETAILS_SCREEN_ROUTE =
        "$CHAT_SCREEN_ROOT/{$ADDRESS_PARAM}"

    override fun route() = DETAILS_SCREEN_ROUTE

    fun creteChatRoute(address: String) =
        "$CHAT_SCREEN_ROOT/$address"
}
