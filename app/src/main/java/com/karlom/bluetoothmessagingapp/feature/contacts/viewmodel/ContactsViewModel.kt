package com.karlom.bluetoothmessagingapp.feature.contacts.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.map
import com.karlom.bluetoothmessagingapp.core.base.BaseViewModel
import com.karlom.bluetoothmessagingapp.core.navigation.NavigationEvent.Destination
import com.karlom.bluetoothmessagingapp.core.navigation.Navigator
import com.karlom.bluetoothmessagingapp.domain.contacts.usecase.GetContacts
import com.karlom.bluetoothmessagingapp.feature.addDevice.router.AddDeviceScreenRouter
import com.karlom.bluetoothmessagingapp.feature.chat.router.ChatRouter
import com.karlom.bluetoothmessagingapp.feature.contacts.mappers.ContactUiMapper
import com.karlom.bluetoothmessagingapp.feature.contacts.models.ContactScreenEvent
import com.karlom.bluetoothmessagingapp.feature.contacts.models.ContactScreenEvent.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val getContacts: GetContacts,
    private val contactMapper: ContactUiMapper,
    private val navigator: Navigator,
) : BaseViewModel<ContactScreenEvent>() {

    val contacts = getContacts()
        .map { page -> page.map { contact -> contactMapper.map(contact) } }

    override fun onEvent(event: ContactScreenEvent) {
        when (event) {
            is OnAddContactClicked -> viewModelScope.launch {
                navigator.emitDestination(Destination(AddDeviceScreenRouter.route()))
            }

            is OnContactClicked -> viewModelScope.launch {
                navigator.emitDestination(
                    Destination(
                        ChatRouter.creteChatRoute(
                            contactName = event.contactName,
                            address = event.address,
                        )
                    )
                )
            }
        }
    }
}
