package com.karlom.bluetoothmessagingapp.feature.contacts.models

import com.karlomaricevic.domain.contacts.models.Contact

abstract class ContactScreenState {
    object Loading: ContactScreenState()
    data class Content(val contacts: List<Contact>): ContactScreenState()
}
