package com.karlom.bluetoothmessagingapp.data.contact

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import arrow.core.Either
import com.karlom.bluetoothmessagingapp.core.models.Failure
import com.karlom.bluetoothmessagingapp.data.shared.db.dao.ContactDao
import com.karlom.bluetoothmessagingapp.data.shared.db.enteties.ContactEntity
import com.karlom.bluetoothmessagingapp.domain.contacts.models.Contact
import kotlinx.coroutines.flow.map
import java.lang.Exception
import javax.inject.Inject

class ContactRepository @Inject constructor(
    private val contactDao: ContactDao,
) {

    private companion object {
        const val PAGE_SIZE = 20
    }

    suspend fun addContact(contact: Contact) = try {
        contactDao.insertAll(
            ContactEntity(
                address = contact.address,
                name = contact.name,
            )
        )
        Either.Right(Unit)
    } catch (e: Exception) {
        Either.Left(Failure.ErrorMessage(e.message ?: e::class.java.name))
    }

    fun getContacts() = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
        pagingSourceFactory = { contactDao.getContactPagingSource() },
    ).flow.map { page -> page.map { entity -> Contact.from(entity) } }
}
