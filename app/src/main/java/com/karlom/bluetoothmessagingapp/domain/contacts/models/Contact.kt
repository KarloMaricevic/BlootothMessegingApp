package com.karlom.bluetoothmessagingapp.domain.contacts.models

import com.karlom.bluetoothmessagingapp.data.shared.db.enteties.ContactEntity

data class Contact(
    val name: String,
    val address: String,
) {
    companion object {

        fun from(entity: ContactEntity) = Contact(
            entity.name,
            entity.address,
        )
    }
}
