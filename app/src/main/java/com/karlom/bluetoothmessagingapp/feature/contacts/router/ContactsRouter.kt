package com.karlom.bluetoothmessagingapp.feature.contacts.router

import com.karlom.bluetoothmessagingapp.core.navigation.NavigationDestination

object ContactsRouter : NavigationDestination {

    private const val CONTACTS_SCREEN_ROOT = "contacts"

    override fun route() = CONTACTS_SCREEN_ROOT
}
