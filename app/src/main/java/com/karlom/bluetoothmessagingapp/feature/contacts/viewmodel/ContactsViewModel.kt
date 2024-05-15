package com.karlom.bluetoothmessagingapp.feature.contacts.viewmodel

import androidx.paging.map
import com.karlom.bluetoothmessagingapp.core.base.BaseViewModel
import com.karlom.bluetoothmessagingapp.domain.contacts.usecase.GetContacts
import com.karlom.bluetoothmessagingapp.feature.contacts.mappers.ContactUiMapper
import com.karlom.bluetoothmessagingapp.feature.contacts.models.ContactScreenEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val getContacts: GetContacts,
    private val contactMapper: ContactUiMapper,
) : BaseViewModel<ContactScreenEvent>() {

    val contacts = getContacts()
        .map { page -> page.map { contact -> contactMapper.map(contact) } }

    override fun onEvent(event: ContactScreenEvent) {
        when (event) {
            else -> {}
        }
    }
}
