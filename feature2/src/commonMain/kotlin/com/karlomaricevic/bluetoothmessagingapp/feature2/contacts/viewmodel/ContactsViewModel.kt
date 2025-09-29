package com.karlomaricevic.bluetoothmessagingapp.feature2.contacts.viewmodel

import androidx.lifecycle.viewModelScope
import com.karlomaricevic.bluetoothmessagingapp.feature2.contacts.mappers.ContactUiMapper
import com.karlomaricevic.bluetoothmessagingapp.feature2.contacts.models.ContactScreenEvent
import com.karlomaricevic.bluetoothmessagingapp.feature2.contacts.models.ContactScreenEvent.*
import com.karlomaricevic.bluetoothmessagingapp.feature2.contacts.navigation.ContactsNavigator
import com.karlomaricevic.bluetoothmessagingapp.domain.contacts.usecase.GetContacts
import com.karlomaricevic.bluetoothmessagingapp.feature2.utils.viewmodel.BaseViewModel
import kotlinx.coroutines.CoroutineScope
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
            is OnAddContactClicked -> launch { navigator.navigateToAddDeviceScreen() }

            is OnContactClicked -> launch {
                navigator.navigateToChatScreen(
                    contactName = event.contactName,
                    address = event.address,
                )
            }
        }
    }
}
