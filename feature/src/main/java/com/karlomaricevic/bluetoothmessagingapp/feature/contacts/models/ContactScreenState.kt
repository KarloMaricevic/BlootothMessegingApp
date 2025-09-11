package com.karlomaricevic.bluetoothmessagingapp.feature.contacts.models

import com.karlomaricevic.bluetoothmessagingapp.domain.contacts.models.Contact

abstract class ContactScreenState {
    object Loading: ContactScreenState()
    data class Content(val contacts: List<Contact>): ContactScreenState()
}
