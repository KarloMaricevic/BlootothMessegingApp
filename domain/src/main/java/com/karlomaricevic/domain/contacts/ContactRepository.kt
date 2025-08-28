package com.karlomaricevic.domain.contacts

import arrow.core.Either
import com.karlomaricevic.domain.contacts.models.Contact
import com.karlomaricevic.domain.core.models.Failure
import kotlinx.coroutines.flow.Flow

interface ContactRepository {
    suspend fun addContact(contact: Contact): Either<Failure, Unit>
    fun getContacts(): Flow<List<Contact>>
}
