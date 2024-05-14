package com.karlom.bluetoothmessagingapp.feature.contacts.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.paging.PagingData
import com.karlom.bluetoothmessagingapp.core.base.BaseViewModel
import com.karlom.bluetoothmessagingapp.domain.contacts.models.Contact
import com.karlom.bluetoothmessagingapp.feature.contacts.models.ContactScreenEvent
import com.karlom.bluetoothmessagingapp.feature.contacts.models.ContactUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor() : BaseViewModel<ContactScreenEvent>() {

    val contacts = flowOf(
        PagingData.from(
            listOf(
                ContactUi(
                    contact = Contact(name = "Contact 1", address = "FA:11"),
                    lastMessage = "You: Message sent",
                    color = Color.Yellow,
                ),
                ContactUi(
                    contact = Contact(name = "Contact 2", address = "EA:77"),
                    lastMessage = "You: How iss your day?",
                    color = Color.Blue,
                )
            )
        )
    )

    override fun onEvent(event: ContactScreenEvent) {
        when (event) {
            else -> {}
        }
    }
}
