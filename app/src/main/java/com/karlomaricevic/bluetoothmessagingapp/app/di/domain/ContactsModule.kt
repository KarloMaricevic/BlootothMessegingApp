package com.karlomaricevic.bluetoothmessagingapp.app.di.domain

import com.karlomaricevic.bluetoothmessagingapp.domain.contacts.ContactRepository
import com.karlomaricevic.bluetoothmessagingapp.domain.contacts.usecase.AddContact
import com.karlomaricevic.bluetoothmessagingapp.domain.contacts.usecase.GetContacts
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object ContactsModule {

    @Provides
    fun providesAddContact(repository: ContactRepository) = AddContact(repository)

    @Provides
    fun providesGetContact(repository: ContactRepository) = GetContacts(repository)
}
