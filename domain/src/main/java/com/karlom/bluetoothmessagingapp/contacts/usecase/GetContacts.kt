package com.karlom.bluetoothmessagingapp.domain.contacts.usecase

import com.karlom.bluetoothmessagingapp.data.contact.ContactRepository
import javax.inject.Inject

class GetContacts @Inject constructor(
    private val repository: ContactRepository,
) {

    operator fun invoke() = repository.getContacts()
}
