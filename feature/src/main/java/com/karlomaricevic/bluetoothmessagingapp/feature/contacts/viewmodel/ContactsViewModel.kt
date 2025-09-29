package com.karlomaricevic.bluetoothmessagingapp.feature.contacts.viewmodel

import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.mappers.ContactUiMapper
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.models.ContactScreenEvent
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.models.ContactScreenEvent.*
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.navigation.ContactsNavigator
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.TIMEOUT_DELAY
import com.karlomaricevic.bluetoothmessagingapp.domain.contacts.usecase.GetContacts
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.NewBaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class ContactsViewModel(
    private val getContacts: GetContacts,
    private val contactMapper: ContactUiMapper,
    private val navigator: ContactsNavigator,
    private val vmScope: CoroutineScope,
) : NewBaseViewModel<ContactScreenEvent>(vmScope) {

    val contacts = getContacts()
        .map { contacts -> contacts.map { contact -> contactMapper.map(contact) } }
        .stateIn(
            scope = vmScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_DELAY),
            initialValue = null,
        )

    override fun onEvent(event: ContactScreenEvent) {
        when (event) {
            is OnAddContactClicked -> launch {
                val i  = vmScope.coroutineContext
                navigator.navigateToAddDeviceScreen()
            }

            is OnContactClicked -> launch {
                navigator.navigateToChatScreen(
                    contactName = event.contactName,
                    address = event.address,
                )
            }
        }
    }
}
