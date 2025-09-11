package com.karlomaricevic.bluetoothmessagingapp.feature.contacts.navigation

import com.karlomaricevic.core.navigation.NavigationDestination

object ContactsRouter : NavigationDestination {

    private const val CONTACTS_SCREEN_ROOT = "contacts"

    override fun route() = CONTACTS_SCREEN_ROOT
}
