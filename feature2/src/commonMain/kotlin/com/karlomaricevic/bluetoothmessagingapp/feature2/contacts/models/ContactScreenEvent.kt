package com.karlomaricevic.bluetoothmessagingapp.feature2.contacts.models

sealed interface ContactScreenEvent {

    data object OnAddContactClicked : ContactScreenEvent

    data class OnContactClicked(
        val contactName: String,
        val address: String
    ) : ContactScreenEvent
}
