package com.karlom.bluetoothmessagingapp.feature.contacts.models

sealed interface ContactScreenEvent {

    data object OnAddContactClicked : ContactScreenEvent
}
