package com.karlomaricevic.bluetoothmessagingapp.data.contact

import com.karlomaricevic.bluetoothmessagingapp.dispatchers.IoDispatcherTag
import com.karlomaricevic.bluetoothmessagingapp.domain.contacts.ContactRepository
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

val contactsDataModule = DI.Module("ContactsDataModule") {
    bind<ContactRepository>() with singleton {
        ContactRepositoryImpl(
            queries = instance(),
            ioDispatcher = instance(tag = IoDispatcherTag),
        )
    }
}
