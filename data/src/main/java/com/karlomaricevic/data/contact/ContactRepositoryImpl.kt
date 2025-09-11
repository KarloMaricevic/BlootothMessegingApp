package com.karlomaricevic.data.contact

import arrow.core.left
import arrow.core.right
import com.karlomaricevic.data.db.daos.ContactDao
import com.karlomaricevic.data.db.entites.ContactEntity
import com.karlomaricevic.bluetoothmessagingapp.domain.contacts.ContactRepository
import com.karlomaricevic.bluetoothmessagingapp.domain.contacts.models.Contact
import com.karlomaricevic.bluetoothmessagingapp.domain.core.models.Failure.ErrorMessage
import kotlinx.coroutines.flow.map
import java.lang.Exception

class ContactRepositoryImpl(
    private val contactDao: ContactDao,
) : ContactRepository {

    override suspend fun addContact(contact: Contact) = try {
        contactDao.insertAll(
            ContactEntity(
                address = contact.address,
                name = contact.name,
            )
        )
        Unit.right()
    } catch (e: Exception) {
        ErrorMessage(e.message ?: e::class.java.name).left()
    }

    override fun getContacts() = contactDao.getAllContactsFlow().map { entities ->
        entities.map { entity ->
            Contact(
                name = entity.name,
                address = entity.address,
            )
        }
    }
}
