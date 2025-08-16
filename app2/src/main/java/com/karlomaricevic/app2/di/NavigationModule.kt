package com.karlomaricevic.app2.di

import com.karlom.bluetoothmessagingapp.feature.addDevice.navigation.AddDeviceNavigator
import com.karlom.bluetoothmessagingapp.feature.chat.navigation.ChatNavigator
import com.karlom.bluetoothmessagingapp.feature.contacts.navigation.ContactsNavigator
import com.karlomaricevic.app2.navigation.Navigator
import com.karlomaricevic.app2.navigation.NavigatorImpl
import com.karlomaricevic.app2.navigation.navigators.AddDeviceNavigatorImpl
import com.karlomaricevic.app2.navigation.navigators.ChatNavigatorImpl
import com.karlomaricevic.app2.navigation.navigators.ContactNavigatorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NavigationModule {

    @Binds
    @Singleton
    abstract fun bindNavigator(navigatorImpl: NavigatorImpl): Navigator

    @Binds
    @Singleton
    abstract fun bindsAddDeviceNavigator(
        addDeviceNavigatorImpl: AddDeviceNavigatorImpl,
    ): AddDeviceNavigator

    @Binds
    @Singleton
    abstract fun bindsChatNavigator(
        chatNavigator: ChatNavigatorImpl,
    ): ChatNavigator

    @Binds
    @Singleton
    abstract fun bindsContactsNavigator(
        contactsNavigator: ContactNavigatorImpl,
    ): ContactsNavigator
}
