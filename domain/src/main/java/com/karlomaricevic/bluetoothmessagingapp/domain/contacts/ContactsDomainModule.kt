package com.karlomaricevic.bluetoothmessagingapp.domain.contacts

import com.karlomaricevic.bluetoothmessagingapp.domain.contacts.usecase.AddContact
import com.karlomaricevic.bluetoothmessagingapp.domain.contacts.usecase.GetContacts
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

val contactsDomainModule = DI.Module("ContactsDomainModule") {
    bind<AddContact>() with provider { AddContact(instance()) }
    bind<GetContacts>() with provider { GetContacts(instance()) }
}
