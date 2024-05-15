package com.karlom.bluetoothmessagingapp.feature.contacts.mappers

import com.karlom.bluetoothmessagingapp.designSystem.theme.blue
import com.karlom.bluetoothmessagingapp.designSystem.theme.gray300
import com.karlom.bluetoothmessagingapp.designSystem.theme.red
import com.karlom.bluetoothmessagingapp.domain.contacts.models.Contact
import com.karlom.bluetoothmessagingapp.feature.contacts.models.ContactUi
import javax.inject.Inject

class ContactUiMapper @Inject constructor() {

    private var numberOfContactsMapped = 0

    fun map(contact: Contact): ContactUi {
        numberOfContactsMapped++
        return ContactUi(
            contact = contact,
            color = getColor(numberOfContactsMapped),
            lastMessage = "Last message placeholder",
        )
    }

    private fun getColor(position: Int) =
        when (position % 2) {
            0 -> gray300
            1 -> blue
            else -> red
        }
}
