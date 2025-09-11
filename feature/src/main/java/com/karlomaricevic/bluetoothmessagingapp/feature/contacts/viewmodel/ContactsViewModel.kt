package com.karlomaricevic.bluetoothmessagingapp.feature.contacts.viewmodel

import androidx.lifecycle.viewModelScope
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.mappers.ContactUiMapper
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.models.ContactScreenEvent
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.models.ContactScreenEvent.*
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.navigation.ContactsNavigator
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.BaseViewModel
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.TIMEOUT_DELAY
import com.karlomaricevic.bluetoothmessagingapp.domain.contacts.usecase.GetContacts
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class ContactsViewModel(
    private val getContacts: GetContacts,
    private val contactMapper: ContactUiMapper,
    private val navigator: ContactsNavigator,
) : BaseViewModel<ContactScreenEvent>() {

    val contacts = getContacts()
        .map { contacts -> contacts.map { contact -> contactMapper.map(contact) } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_DELAY),
            initialValue = null,
        )

    override fun onEvent(event: ContactScreenEvent) {
        when (event) {
            is OnAddContactClicked -> viewModelScope.launch {
                navigator.navigateToAddDeviceScreen()
            }

            is OnContactClicked -> viewModelScope.launch {
                navigator.navigateToChatScreen(
                    contactName = event.contactName,
                    address = event.address,
                )
            }
        }
    }
}
