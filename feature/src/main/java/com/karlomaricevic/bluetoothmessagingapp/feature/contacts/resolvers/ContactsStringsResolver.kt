package com.karlomaricevic.bluetoothmessagingapp.feature.contacts.resolvers

import android.content.Context
import com.karlomaricevic.bluetoothmessagingapp.feature.R
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.resolvers.models.ContactsStringKeys
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.resolvers.models.ContactsStringKeys.ADD_CONTACTS_BUTTON
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.resolvers.models.ContactsStringKeys.CHAT_TITLE
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.resolvers.models.ContactsStringKeys.DEFAULT_ICON_CONTENT_DESCRIPTION
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.resolvers.models.ContactsStringKeys.NEW_CHAT
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.resolvers.models.ContactsStringKeys.NO_CONTACTS_MESSAGE
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.StringResolver

class ContactsStringsResolver(private val context: Context) : StringResolver<ContactsStringKeys> {

    override fun getString(identifier: ContactsStringKeys): String = when (identifier) {
        CHAT_TITLE -> context.getString(R.string.contacts_screen_chat_title)
        NO_CONTACTS_MESSAGE -> context.getString(R.string.contacts_screen_no_contacts)
        ADD_CONTACTS_BUTTON -> context.getString(R.string.contacts_screen_add_contacts)
        DEFAULT_ICON_CONTENT_DESCRIPTION -> context.getString(R.string.default_icon_content_description)
        NEW_CHAT -> context.getString(R.string.contacts_screen_new_chat)
    }
}
