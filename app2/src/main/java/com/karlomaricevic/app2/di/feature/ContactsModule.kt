package com.karlomaricevic.app2.di.feature

import com.karlomaricevic.app2.navigation.Navigator
import com.karlomaricevic.app2.navigation.navigators.ContactNavigatorImpl
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.mappers.ContactUiMapper
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.navigation.ContactsNavigator
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface ContactsModule {


    companion object {

        @Provides
        fun providesContactsNavigatorImpl(navigator: Navigator) = ContactNavigatorImpl(navigator)

        @Provides
        fun providesContactUIMapper() = ContactUiMapper()
    }

    @Binds
    fun bindsContactNavigator(contactNavigatorImpl: ContactNavigatorImpl): ContactsNavigator
}