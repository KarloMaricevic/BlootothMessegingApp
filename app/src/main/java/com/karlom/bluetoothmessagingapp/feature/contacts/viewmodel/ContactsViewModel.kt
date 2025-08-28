package com.karlom.bluetoothmessagingapp.feature.contacts.viewmodel

import androidx.lifecycle.viewModelScope
import com.karlom.bluetoothmessagingapp.core.base.BaseViewModel
import com.karlom.bluetoothmessagingapp.core.base.TIMEOUT_DELAY
import com.karlom.bluetoothmessagingapp.feature.contacts.mappers.ContactUiMapper
import com.karlom.bluetoothmessagingapp.feature.contacts.models.ContactScreenEvent
import com.karlom.bluetoothmessagingapp.feature.contacts.models.ContactScreenEvent.*
import com.karlom.bluetoothmessagingapp.feature.contacts.navigation.ContactsNavigator
import com.karlomaricevic.domain.contacts.usecase.GetContacts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class ContactsViewModel @Inject constructor(
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
