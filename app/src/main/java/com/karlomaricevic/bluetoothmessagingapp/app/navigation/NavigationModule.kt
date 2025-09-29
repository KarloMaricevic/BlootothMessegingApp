package com.karlomaricevic.bluetoothmessagingapp.app.navigation

import com.karlomaricevic.bluetoothmessagingapp.app.navigation.navigators.AddDeviceNavigatorImpl
import com.karlomaricevic.bluetoothmessagingapp.app.navigation.navigators.ChatNavigatorImpl
import com.karlomaricevic.bluetoothmessagingapp.app.navigation.navigators.ContactNavigatorImpl
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.navigation.AddDeviceNavigator
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.navigation.ChatNavigator
import com.karlomaricevic.bluetoothmessagingapp.feature2.contacts.navigation.ContactsNavigator
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider
import org.kodein.di.singleton

val navigationModule = DI.Module("navigationModule") {
    bind<Navigator>() with singleton { NavigatorImpl() }

    bind<AddDeviceNavigator>() with provider { AddDeviceNavigatorImpl(instance()) }

    bind<ChatNavigator>() with provider { ChatNavigatorImpl(instance()) }

    bind<ContactsNavigator>() with provider { ContactNavigatorImpl(instance()) }
}
