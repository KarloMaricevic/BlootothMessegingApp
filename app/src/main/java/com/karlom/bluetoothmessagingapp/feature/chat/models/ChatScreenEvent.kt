package com.karlom.bluetoothmessagingapp.feature.chat.models

sealed interface ChatScreenEvent {


    data class OnTextChanged(val text: String) : ChatScreenEvent

    data object OnSendClicked : ChatScreenEvent
}
