package com.karlomaricevic.bluetoothmessagingapp.feature2.contacts.navigation

import com.karlomaricevic.bluetoothmessagingapp.core.navigation.NavigationDestination

object ContactsRouter : NavigationDestination {

    private const val CONTACTS_SCREEN_ROOT = "contacts"

    override fun route() = CONTACTS_SCREEN_ROOT
}
