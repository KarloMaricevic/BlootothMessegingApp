package com.karlomaricevic.bluetoothmessagingapp.feature.contacts

import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.mappers.ContactUiMapper
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.viewmodel.ContactsViewModel
import kotlinx.coroutines.CoroutineScope
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance
import org.kodein.di.provider

val contactsScreenModule = DI.Module("ContactsScreenModule") {
    bind<ContactUiMapper>() with provider { ContactUiMapper() }
    bind<ContactsViewModel>() with factory { vmScope: CoroutineScope ->
        ContactsViewModel(
            getContacts = instance(),
            contactMapper = instance(),
            navigator = instance(),
            vmScope = vmScope,
        )
    }
}
