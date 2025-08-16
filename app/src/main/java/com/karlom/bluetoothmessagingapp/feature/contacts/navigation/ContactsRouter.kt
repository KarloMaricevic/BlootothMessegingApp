package com.karlom.bluetoothmessagingapp.feature.contacts.navigation

import com.karlomaricevic.core_navigation.NavigationDestination

object ContactsRouter : NavigationDestination {

    private const val CONTACTS_SCREEN_ROOT = "contacts"

    override fun route() = CONTACTS_SCREEN_ROOT
}
