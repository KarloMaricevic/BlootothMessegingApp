package com.karlomaricevic.bluetoothmessagingapp.data.contact

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.karlomaricevic.bluetoothmessagingapp.data.db.ContactQueries
import com.karlomaricevic.bluetoothmessagingapp.domain.contacts.ContactRepository
import com.karlomaricevic.bluetoothmessagingapp.domain.contacts.models.Contact
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class ContactRepositoryImpl(
    private val queries: ContactQueries,
    private val ioDispatcher: CoroutineDispatcher,
) : ContactRepository {

    override suspend fun addContact(contact: Contact) {
        withContext(ioDispatcher) {
            queries.insertContact(
                address = contact.address,
                name = contact.name,
            )
        }
    }

    override fun getContacts() = queries.selectAllContacts()
        .asFlow()
        .mapToList(ioDispatcher)
        .map { entities -> entities.map { entity ->
            Contact(
                name = entity.name,
                address = entity.address,
            )
        }
    }
}
