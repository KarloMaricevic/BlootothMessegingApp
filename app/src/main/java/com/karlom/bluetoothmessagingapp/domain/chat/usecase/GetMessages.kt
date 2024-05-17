package com.karlom.bluetoothmessagingapp.domain.chat.usecase

import com.karlom.bluetoothmessagingapp.data.chat.ChatRepository
import com.karlom.bluetoothmessagingapp.domain.contacts.models.Contact
import javax.inject.Inject

class GetMessages @Inject constructor(
    private val repository: ChatRepository,
) {

    operator fun invoke(contactAddress: String) = repository.getMessages(contactAddress)
}
