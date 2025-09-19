package com.karlomaricevic.bluetoothmessagingapp.feature.contacts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.models.ContactScreenEvent
import kotlinx.coroutines.CoroutineScope

class AndroidContactsViewModel(vmFactory: (CoroutineScope) -> ContactsViewModel) : ViewModel() {

    private val sharedVM: ContactsViewModel = vmFactory(viewModelScope)

    val contacts = sharedVM.contacts

    fun onEvent(event: ContactScreenEvent) {
        sharedVM.onEvent(event)
    }
}
