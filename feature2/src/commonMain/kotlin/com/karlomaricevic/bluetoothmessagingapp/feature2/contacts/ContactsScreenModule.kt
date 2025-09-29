package com.karlomaricevic.bluetoothmessagingapp.feature2.contacts

import com.karlomaricevic.bluetoothmessagingapp.feature2.contacts.mappers.ContactUiMapper
import com.karlomaricevic.bluetoothmessagingapp.feature2.contacts.viewmodel.ContactsViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

val contactsScreenModule = DI.Module("ContactsScreenModule") {
    bind<ContactUiMapper>() with provider { ContactUiMapper() }
    bind<ContactsViewModel>() with provider {
        ContactsViewModel(
            getContacts = instance(),
            contactMapper = instance(),
            navigator = instance(),
        )
    }
}
