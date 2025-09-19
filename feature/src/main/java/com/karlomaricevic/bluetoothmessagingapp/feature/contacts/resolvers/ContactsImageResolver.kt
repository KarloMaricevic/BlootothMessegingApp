package com.karlomaricevic.bluetoothmessagingapp.feature.contacts.resolvers

import com.karlomaricevic.bluetoothmessagingapp.feature.R
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.resolvers.models.ContactsImageKeys
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.resolvers.models.ContactsImageKeys.*
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.ImageResolver
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.models.ImageResource
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.models.ImageResource.Android

class ContactsImageResolver : ImageResolver<ContactsImageKeys> {

    override fun getImage(identifier: ContactsImageKeys): ImageResource = when (identifier) {
        NO_CONTACTS_ICON -> Android(R.drawable.ic_mail_box)
        CHAT_ICON -> Android(R.drawable.ic_chat)
    }
}
