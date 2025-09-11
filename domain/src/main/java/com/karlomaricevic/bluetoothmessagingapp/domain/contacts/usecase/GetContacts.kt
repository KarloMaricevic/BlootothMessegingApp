package com.karlomaricevic.bluetoothmessagingapp.domain.contacts.usecase

import com.karlomaricevic.bluetoothmessagingapp.domain.contacts.ContactRepository

class GetContacts(
    private val repository: ContactRepository,
) {

    operator fun invoke() = repository.getContacts()
}
