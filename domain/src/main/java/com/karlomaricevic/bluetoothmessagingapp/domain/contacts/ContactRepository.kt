package com.karlomaricevic.bluetoothmessagingapp.domain.contacts

import arrow.core.Either
import com.karlomaricevic.bluetoothmessagingapp.domain.contacts.models.Contact
import com.karlomaricevic.bluetoothmessagingapp.domain.core.models.Failure
import kotlinx.coroutines.flow.Flow

interface ContactRepository {
    suspend fun addContact(contact: Contact)
    fun getContacts(): Flow<List<Contact>>
}
