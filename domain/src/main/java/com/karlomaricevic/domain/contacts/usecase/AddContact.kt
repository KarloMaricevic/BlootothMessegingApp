package com.karlomaricevic.domain.contacts.usecase

import com.karlomaricevic.domain.contacts.models.Contact
import com.karlomaricevic.domain.contacts.ContactRepository

class AddContact(
    private val repository: ContactRepository,
) {

    suspend operator fun invoke(contact: Contact) =
        repository.addContact(contact)
}
