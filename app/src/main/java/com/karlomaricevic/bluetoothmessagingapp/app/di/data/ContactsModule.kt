package com.karlomaricevic.bluetoothmessagingapp.app.di.data

import com.karlomaricevic.bluetoothmessagingapp.data.db.daos.ContactDao
import com.karlomaricevic.bluetoothmessagingapp.data.contact.ContactRepositoryImpl
import com.karlomaricevic.bluetoothmessagingapp.domain.contacts.ContactRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ContactsModule {

    companion object {

        @Provides
        @Singleton
        fun providesContactRepositoryImpl(dao: ContactDao) = ContactRepositoryImpl(dao)
    }

    @Binds
    @Singleton
    fun bindsContactRepository(contactRepositoryImpl: ContactRepositoryImpl) : ContactRepository
}
