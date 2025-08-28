package com.karlomaricevic.domain.contacts.usecase

import com.karlomaricevic.domain.contacts.ContactRepository

class GetContacts(
    private val repository: ContactRepository,
) {

    operator fun invoke() = repository.getContacts()
}
