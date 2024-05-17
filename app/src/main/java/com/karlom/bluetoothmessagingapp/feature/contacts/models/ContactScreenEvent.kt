package com.karlom.bluetoothmessagingapp.feature.contacts.models

sealed interface ContactScreenEvent {

    data object OnAddContactClicked : ContactScreenEvent

    data class OnContactClicked(val address: String) : ContactScreenEvent
}
