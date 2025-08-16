package com.karlom.bluetoothmessagingapp.domain.contacts.usecase

import com.karlom.bluetoothmessagingapp.data.contact.ContactRepository
import com.karlom.bluetoothmessagingapp.domain.contacts.models.Contact
import javax.inject.Inject

class AddContact @Inject constructor(
    private val repository: ContactRepository,
) {

    suspend operator fun invoke(contact: Contact) =
        repository.addContact(contact)
}
