package com.karlomaricevic.bluetoothmessagingapp.domain.contacts.usecase

import com.karlomaricevic.bluetoothmessagingapp.domain.contacts.models.Contact
import com.karlomaricevic.bluetoothmessagingapp.domain.contacts.ContactRepository

class AddContact(
    private val repository: ContactRepository,
) {

    suspend operator fun invoke(contact: Contact) =
        repository.addContact(contact)
}
